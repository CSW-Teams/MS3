package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.broker.mapper.AiScheduleResponseMapper;
import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleVariantsResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentBrokerImpl implements AgentBroker {

    private final AiBrokerProperties properties;
    private final Map<AgentProvider, AgentProviderAdapter> adapters;
    private final AiScheduleJsonParser jsonParser;
    private final AiScheduleResponseMapper mapper = new AiScheduleResponseMapper();

    public AgentBrokerImpl(AiBrokerProperties properties,
                           List<AgentProviderAdapter> adapters,
                           AiScheduleJsonParser jsonParser) {
        this.properties = properties;
        this.jsonParser = jsonParser;
        this.adapters = new EnumMap<>(AgentProvider.class);
        for (AgentProviderAdapter adapter : adapters) {
            this.adapters.put(adapter.provider(), adapter);
        }
    }

    @Override
    public AiScheduleVariantsResponse requestSchedule(AiBrokerRequest request) {
        validateRequest(request);
        AgentProvider provider = properties.getProvider();
        AgentProviderAdapter adapter = adapters.get(provider);
        if (adapter == null) {
            throw AiProtocolException.businessFailure("No adapter configured for provider " + provider);
        }
        return executeWithRetry(adapter, request);
    }

    private AiScheduleVariantsResponse executeWithRetry(AgentProviderAdapter adapter, AiBrokerRequest request) {
        Instant start = Instant.now();
        Duration totalTimeout = properties.getTotalTimeout();
        int maxRetries = properties.getMaxRetries();
        Duration backoff = properties.getRetryBackoff();
        AiProtocolException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            if (isTotalTimeoutExceeded(start, totalTimeout)) {
                throw AiProtocolException.timeout("AI broker total timeout exceeded", lastException);
            }
            try {
                String rawJson = adapter.execute(request);
                if (isTotalTimeoutExceeded(start, totalTimeout)) {
                    throw AiProtocolException.timeout("AI broker total timeout exceeded", lastException);
                }
                AiScheduleVariantsResponseDto dto = jsonParser.parseVariants(rawJson);
                return mapVariants(dto);
            } catch (AiProtocolException ex) {
                lastException = ex;
            } catch (RuntimeException ex) {
                lastException = AiProtocolException.transportFailure("AI provider call failed", ex);
            }

            if (attempt < maxRetries) {
                sleep(backoff);
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
                throw AiProtocolException.partialSuccess("AI response marked PARTIAL_SUCCESS for variant " + entry.getKey());
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

    private static void sleep(Duration backoff) {
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
