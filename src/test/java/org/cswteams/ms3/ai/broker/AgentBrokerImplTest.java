package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgentBrokerImplTest {

    @Test
    void routesToConfiguredProvider() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);

        RecordingAdapter gemma = new RecordingAdapter(AgentProvider.GEMMA, successJson());
        RecordingAdapter llama = new RecordingAdapter(AgentProvider.LLAMA_70B, successJson());

        AgentBroker broker = new AgentBrokerImpl(properties, List.of(gemma, llama), new AiScheduleJsonParser());
        AiScheduleResponse response = broker.requestSchedule(AiBrokerRequest.forToon("toon"));

        assertNotNull(response);
        assertEquals(1, gemma.invocations());
        assertEquals(0, llama.invocations());
    }

    @Test
    void retriesUntilSuccess() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.LLAMA_70B);
        properties.setMaxRetries(2);
        properties.setRetryBackoff(Duration.ZERO);

        FailingThenSuccessAdapter adapter = new FailingThenSuccessAdapter(AgentProvider.LLAMA_70B, 2, successJson());
        AgentBroker broker = new AgentBrokerImpl(properties, List.of(adapter), new AiScheduleJsonParser());

        AiScheduleResponse response = broker.requestSchedule(AiBrokerRequest.forToon("toon"));

        assertNotNull(response);
        assertEquals(3, adapter.invocations());
    }

    @Test
    void throwsOnPartialSuccess() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);

        RecordingAdapter adapter = new RecordingAdapter(AgentProvider.GEMMA, partialJson());
        AgentBroker broker = new AgentBrokerImpl(properties, List.of(adapter), new AiScheduleJsonParser());

        AiProtocolException exception = assertThrows(AiProtocolException.class,
                () -> broker.requestSchedule(AiBrokerRequest.forToon("toon")));

        assertEquals(AiProtocolException.ErrorCode.PARTIAL_SUCCESS, exception.getCode());
    }

    private static String successJson() {
        return "{\"status\":\"SUCCESS\",\"metadata\":{\"reasoning\":\"ok\",\"optimality_score\":0.9,\"metrics\":{\"coverage_percent\":1.0,\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":1.0,\"final\":0.5}},\"soft_violations_count\":0}},\"assignments\":[],\"uncovered_shifts\":[],\"uffa_delta\":[]}";
    }

    private static String partialJson() {
        return "{\"status\":\"PARTIAL_SUCCESS\",\"metadata\":{\"reasoning\":\"partial\",\"optimality_score\":0.4},\"assignments\":[],\"uncovered_shifts\":[],\"uffa_delta\":[]}";
    }

    private static class RecordingAdapter implements AgentProviderAdapter {
        private final AgentProvider provider;
        private final String response;
        private final AtomicInteger invocations = new AtomicInteger();

        private RecordingAdapter(AgentProvider provider, String response) {
            this.provider = provider;
            this.response = response;
        }

        @Override
        public AgentProvider provider() {
            return provider;
        }

        @Override
        public String execute(AiBrokerRequest request) {
            invocations.incrementAndGet();
            return response;
        }

        int invocations() {
            return invocations.get();
        }
    }

    private static class FailingThenSuccessAdapter implements AgentProviderAdapter {
        private final AgentProvider provider;
        private final int failuresBeforeSuccess;
        private final String response;
        private final AtomicInteger invocations = new AtomicInteger();

        private FailingThenSuccessAdapter(AgentProvider provider, int failuresBeforeSuccess, String response) {
            this.provider = provider;
            this.failuresBeforeSuccess = failuresBeforeSuccess;
            this.response = response;
        }

        @Override
        public AgentProvider provider() {
            return provider;
        }

        @Override
        public String execute(AiBrokerRequest request) {
            int attempt = invocations.incrementAndGet();
            if (attempt <= failuresBeforeSuccess) {
                throw new RuntimeException("temporary failure");
            }
            return response;
        }

        int invocations() {
            return invocations.get();
        }
    }
}
