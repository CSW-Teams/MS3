package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.broker.mapper.AiScheduleResponseMapper;
import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleVariantsResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

/**
 * Transport-layer boundary for AI provider calls.
 *
 * <p>This broker is the sole layer responsible for handling transport-level failures such as timeouts,
 * network errors, and rate limits. It applies retries and converts low-level failures into
 * {@link AiProtocolException} instances for callers to interpret as needed.</p>
 */
public class AgentBrokerImpl implements AgentBroker {

    private static final Logger logger = LoggerFactory.getLogger(AgentBrokerImpl.class);
    private static final long RATE_LIMIT_MIN_BACKOFF_MS = 1_000L;
    private static final int RATE_LIMIT_BACKOFF_MULTIPLIER = 4;
    private static final Duration GEMMA_RATE_LIMIT_RETRY_DELAY = Duration.ofMinutes(1);
    // 429 retries are skipped for oversized requests to avoid prolonged waits for payloads unlikely
    // to succeed within provider token constraints.
    private static final int RATE_LIMIT_RETRY_TOKEN_CUTOFF = 10_000;
    private final AiBrokerProperties properties;
    private final Map<AgentProvider, AgentProviderAdapter> adapters;
    private static final int TOKEN_BUDGET_LIMIT = 15000;

    private final AiScheduleJsonParser jsonParser;
    private final AiScheduleResponseMapper mapper = new AiScheduleResponseMapper();
    private final AiTokenEstimator tokenEstimator;
    private final AiTokenUsageTracker tokenUsageTracker;
    private final DoubleSupplier jitterSource;

    public AgentBrokerImpl(AiBrokerProperties properties,
                           List<AgentProviderAdapter> adapters,
                           AiScheduleJsonParser jsonParser) {
        this(properties, adapters, jsonParser, new AiTokenEstimator(), new AiTokenUsageTracker(), ThreadLocalRandom.current()::nextDouble);
    }

    AgentBrokerImpl(AiBrokerProperties properties,
                    List<AgentProviderAdapter> adapters,
                    AiScheduleJsonParser jsonParser,
                    AiTokenEstimator tokenEstimator,
                    AiTokenUsageTracker tokenUsageTracker) {
        this(properties, adapters, jsonParser, tokenEstimator, tokenUsageTracker, ThreadLocalRandom.current()::nextDouble);
    }

    AgentBrokerImpl(AiBrokerProperties properties,
                    List<AgentProviderAdapter> adapters,
                    AiScheduleJsonParser jsonParser,
                    AiTokenEstimator tokenEstimator,
                    AiTokenUsageTracker tokenUsageTracker,
                    DoubleSupplier jitterSource) {
        this.properties = properties;
        this.jsonParser = jsonParser;
        this.tokenEstimator = tokenEstimator;
        this.tokenUsageTracker = tokenUsageTracker;
        this.jitterSource = jitterSource;
        this.adapters = new EnumMap<>(AgentProvider.class);
        for (AgentProviderAdapter adapter : adapters) {
            this.adapters.put(adapter.provider(), adapter);
        }
    }

    @Override
    public AiTokenBudgetGuardResult previewTokenBudget(AiBrokerRequest request) {
        validateRequest(request);
        AgentProvider provider = properties.getProvider();
        int estimatedInputTokens = tokenEstimator.estimateInputTokens(request);
        int estimatedOutputTokens = tokenEstimator.estimateExpectedOutputTokens(request);
        int projectedTpm = tokenUsageTracker.projectedTpm(provider, estimatedInputTokens, estimatedOutputTokens);
        boolean allowed = projectedTpm <= TOKEN_BUDGET_LIMIT;
        return new AiTokenBudgetGuardResult(
                allowed,
                estimatedInputTokens,
                estimatedOutputTokens,
                projectedTpm,
                TOKEN_BUDGET_LIMIT
        );
    }

    @Override
    public AiScheduleVariantsResponse requestSchedule(AiBrokerRequest request) {
        AgentProvider provider = properties.getProvider();
        AiTokenBudgetGuardResult budgetGuard = previewTokenBudget(request);

        logger.info("event=ai_broker_request_start provider={} correlation_id={} payload_length={} instructions_length={} max_retries={} total_timeout_ms={} estimated_input_tokens={} estimated_output_tokens={} projected_tpm={} budget_limit={}",
                provider,
                request.getCorrelationId(),
                request.getToonPayload() == null ? 0 : request.getToonPayload().length(),
                request.getInstructions() == null ? 0 : request.getInstructions().length(),
                properties.getMaxRetries(),
                properties.getTotalTimeout() == null ? null : properties.getTotalTimeout().toMillis(),
                budgetGuard.getEstimatedInputTokens(),
                budgetGuard.getEstimatedOutputTokens(),
                budgetGuard.getProjectedTpm(),
                budgetGuard.getBudgetLimit());

        enforceTokenBudget(provider, request, budgetGuard);
        tokenUsageTracker.recordUsage(provider, budgetGuard.getEstimatedInputTokens(), budgetGuard.getEstimatedOutputTokens());
        AgentProviderAdapter adapter = adapters.get(provider);
        if (adapter == null) {
            throw AiProtocolException.businessFailure("No adapter configured for provider " + provider);
        }
        return executeWithRetry(adapter, request, budgetGuard);
    }

    private void enforceTokenBudget(AgentProvider provider,
                                    AiBrokerRequest request,
                                    AiTokenBudgetGuardResult budgetGuard) {
        if (budgetGuard.isAllowed()) {
            return;
        }
        logger.warn("event=ai_broker_budget_exceeded provider={} correlation_id={} estimated_input_tokens={} estimated_output_tokens={} projected_tpm={} budget_limit={}",
                provider,
                request.getCorrelationId(),
                budgetGuard.getEstimatedInputTokens(),
                budgetGuard.getEstimatedOutputTokens(),
                budgetGuard.getProjectedTpm(),
                budgetGuard.getBudgetLimit());
        throw AiProtocolException.tokenBudgetExceeded("AI token budget exceeded for rolling 60-second window");
    }

    private AiScheduleVariantsResponse executeWithRetry(AgentProviderAdapter adapter,
                                                        AiBrokerRequest request,
                                                        AiTokenBudgetGuardResult budgetGuard) {
        Instant start = Instant.now();
        Duration totalTimeout = properties.getTotalTimeout();
        int maxRetries = properties.getMaxRetries();
        Duration backoff = properties.getRetryBackoff();
        int estimatedTotalTokens = budgetGuard.getEstimatedInputTokens() + budgetGuard.getEstimatedOutputTokens();
        AiProtocolException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            // The total timeout is an end-to-end budget: provider call time and all retry waits
            // (including GEMMA's 60s 429 delay when retry is allowed) consume the same timer window.
            if (isTotalTimeoutExceeded(start, totalTimeout)) {
                logger.warn("event=ai_broker_timeout_exceeded attempt={} correlation_id={}", attempt, request.getCorrelationId());
                throw AiProtocolException.timeout("AI broker total timeout exceeded", lastException);
            }
            try {
                logger.info("event=ai_broker_attempt_start attempt={} correlation_id={}", attempt, request.getCorrelationId());
                String rawJson = adapter.execute(request);
                if (isTotalTimeoutExceeded(start, totalTimeout)) {
                    logger.warn("event=ai_broker_timeout_exceeded attempt={} correlation_id={}", attempt, request.getCorrelationId());
                    throw AiProtocolException.timeout("AI broker total timeout exceeded", lastException);
                }
                AiScheduleVariantsResponseDto dto = jsonParser.parseVariants(rawJson);
                logger.info("event=ai_broker_attempt_success attempt={} correlation_id={} variants_count={}",
                        attempt,
                        request.getCorrelationId(),
                        dto == null || dto.variants == null ? 0 : dto.variants.size());
                return mapVariants(dto);
            } catch (AiProtocolException ex) {
                logger.warn("event=ai_broker_attempt_failed attempt={} correlation_id={} error_code={}",
                        attempt,
                        request.getCorrelationId(),
                        ex.getCode());
                lastException = ex;
            } catch (RuntimeException ex) {
                logger.warn("event=ai_broker_attempt_failed attempt={} correlation_id={} error_code=TRANSPORT_FAILURE",
                        attempt,
                        request.getCorrelationId());
                lastException = AiProtocolException.transportFailure("AI provider call failed", ex);
            }

            if (attempt < maxRetries) {
                boolean rateLimited = isRateLimitedFailure(lastException);
                if (rateLimited && estimatedTotalTokens > RATE_LIMIT_RETRY_TOKEN_CUTOFF) {
                    logger.warn("event=ai_broker_rate_limit_retry_skipped attempt={} correlation_id={} estimated_total_tokens={} retry_token_cutoff={}",
                            attempt,
                            request.getCorrelationId(),
                            estimatedTotalTokens,
                            RATE_LIMIT_RETRY_TOKEN_CUTOFF);
                    break;
                }
                Duration retryDelay = computeRetryDelay(backoff, attempt, rateLimited, adapter.provider());
                if (rateLimited) {
                    logger.warn("event=ai_broker_rate_limit_backoff attempt={} correlation_id={} backoff_ms={} estimated_total_tokens={}",
                            attempt,
                            request.getCorrelationId(),
                            retryDelay.toMillis(),
                            estimatedTotalTokens);
                }
                sleep(retryDelay);
            }
        }

        if (lastException != null) {
            throw lastException;
        }
        throw AiProtocolException.transportFailure("AI provider call failed", null);
    }

    private AiScheduleVariantsResponse mapVariants(AiScheduleVariantsResponseDto dto) {
        if (dto == null || dto.variants == null || dto.variants.isEmpty()) {
            throw AiProtocolException.schemaMismatch("AI response missing variants", null);
        }
        Map<String, AiScheduleResponse> mapped = new HashMap<>();
        for (Map.Entry<String, AiScheduleResponseDto> entry : dto.variants.entrySet()) {
            AiScheduleResponseDto variant = entry.getValue();
            if (variant == null) {
                throw AiProtocolException.schemaMismatch("AI response variant " + entry.getKey() + " is null", null);
            }
            if (variant.status == AiStatus.PARTIAL_SUCCESS) {
                logger.warn("event=ai_broker_variant_partial_success variant_label={}", entry.getKey());
            }
            if (variant.status == AiStatus.FAILURE) {
                throw AiProtocolException.businessFailure("AI response marked FAILURE for variant " + entry.getKey());
            }
            mapped.put(entry.getKey(), mapper.toDomain(variant));
        }
        return new AiScheduleVariantsResponse(mapped);
    }

    private void validateRequest(AiBrokerRequest request) {
        if (request == null || request.getToonPayload() == null || request.getToonPayload().trim().isEmpty()) {
            throw AiProtocolException.businessFailure("Missing TOON payload for AI broker");
        }
    }

    private static boolean isTotalTimeoutExceeded(Instant start, Duration totalTimeout) {
        // Null or zero means no total timeout is enforced.
        if (totalTimeout == null || totalTimeout.isZero()) {
            return false;
        }
        // Negative timeouts are treated as invalid and immediately exceeded.
        if (totalTimeout.isNegative()) {
            return true;
        }
        return Duration.between(start, Instant.now()).compareTo(totalTimeout) > 0;
    }

    private Duration computeRetryDelay(Duration baseBackoff, int attempt, boolean rateLimited, AgentProvider provider) {
        if (!rateLimited) {
            return normalizeBackoff(baseBackoff);
        }

        if (provider == AgentProvider.GEMMA) {
            return GEMMA_RATE_LIMIT_RETRY_DELAY;
        }

        Duration normalized = normalizeBackoff(baseBackoff);
        long baseMillis = Math.max(normalized.toMillis(), RATE_LIMIT_MIN_BACKOFF_MS);
        long exponentialMillis = safeMultiply(baseMillis, 1L << Math.min(attempt, 30));
        long pacedMillis = safeMultiply(exponentialMillis, RATE_LIMIT_BACKOFF_MULTIPLIER);
        long jitterMillis = (long) (pacedMillis * Math.max(0D, jitterSource.getAsDouble()));
        return Duration.ofMillis(safeAdd(pacedMillis, jitterMillis));
    }

    private static Duration normalizeBackoff(Duration backoff) {
        if (backoff == null || backoff.isZero() || backoff.isNegative()) {
            return Duration.ZERO;
        }
        return backoff;
    }

    private static long safeMultiply(long value, long factor) {
        if (value <= 0 || factor <= 0) {
            return 0;
        }
        if (value > Long.MAX_VALUE / factor) {
            return Long.MAX_VALUE;
        }
        return value * factor;
    }

    private static long safeAdd(long left, long right) {
        if (Long.MAX_VALUE - left < right) {
            return Long.MAX_VALUE;
        }
        return left + right;
    }

    private static boolean isRateLimitedFailure(AiProtocolException exception) {
        if (exception == null) {
            return false;
        }
        if (containsRateLimitMarker(exception.getMessage())) {
            return true;
        }
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (containsRateLimitMarker(cause.getMessage())) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private static boolean containsRateLimitMarker(String message) {
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase();
        return normalized.contains("429") || normalized.contains("too many requests") || normalized.contains("rate limit");
    }

    void sleep(Duration backoff) {
        if (backoff == null || backoff.isZero() || backoff.isNegative()) {
            return;
        }
        try {
            Thread.sleep(backoff.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
