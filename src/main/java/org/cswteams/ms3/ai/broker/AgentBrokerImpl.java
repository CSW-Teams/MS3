package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AgentBrokerImpl implements AgentBroker {
    private final AiBrokerProperties properties;
    private final AiScheduleJsonParser jsonParser;
    private final Map<AiProvider, AgentProviderAdapter> adaptersByProvider;

    public AgentBrokerImpl(AiBrokerProperties properties,
                           AiScheduleJsonParser jsonParser,
                           List<AgentProviderAdapter> adapters) {
        this.properties = properties;
        this.jsonParser = jsonParser;
        this.adaptersByProvider = new EnumMap<>(AiProvider.class);
        for (AgentProviderAdapter adapter : adapters) {
            this.adaptersByProvider.put(adapter.provider(), adapter);
        }
    }

    @Override
    public AiScheduleResponseDto requestSchedule(String requestPayload) {
        AiProvider provider = properties.getProvider();
        AgentProviderAdapter adapter = adaptersByProvider.get(provider);
        if (adapter == null) {
            throw new IllegalStateException("No AI adapter registered for provider: " + provider);
        }
        int maxRetries = Math.max(0, properties.getMaxRetries());
        Duration backoff = properties.getRetryBackoff();
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                String responsePayload = requestWithTotalTimeout(adapter, requestPayload);
                return jsonParser.parse(responsePayload);
            } catch (AiProtocolException ex) {
                throw ex;
            } catch (Exception ex) {
                if (!isRetryable(ex) || attempt == maxRetries) {
                    throw toTransportException(ex);
                }
                applyBackoff(backoff);
            }
        }
        throw AiProtocolException.transportFailure("AI provider request failed.", null);
    }

    private String requestWithTotalTimeout(AgentProviderAdapter adapter, String requestPayload) throws Exception {
        Duration totalTimeout = properties.getTotalTimeout();
        if (totalTimeout == null || totalTimeout.isZero() || totalTimeout.isNegative()) {
            return adapter.requestSchedule(requestPayload);
        }
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> adapter.requestSchedule(requestPayload));
        try {
            return future.get(totalTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            throw ex;
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    private boolean isRetryable(Exception ex) {
        return ex instanceof RestClientException
                || ex instanceof TimeoutException
                || ex instanceof java.io.IOException;
    }

    private void applyBackoff(Duration backoff) {
        if (backoff == null || backoff.isZero() || backoff.isNegative()) {
            return;
        }
        try {
            Thread.sleep(backoff.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private AiProtocolException toTransportException(Exception ex) {
        if (ex instanceof TimeoutException) {
            return AiProtocolException.timeout("AI provider request timed out.", ex);
        }
        return AiProtocolException.transportFailure("AI provider request failed.", ex);
    }
}
