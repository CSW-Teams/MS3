package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AgentBrokerImplTest {

    @Test
    public void requestSchedule_shouldSelectConfiguredProvider() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.LLAMA_70B);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);

        AgentProviderAdapter llamaAdapter = mock(AgentProviderAdapter.class);
        when(llamaAdapter.provider()).thenReturn(AgentProvider.LLAMA_70B);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(llamaAdapter.execute(request)).thenReturn(validJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter, llamaAdapter),
                new AiScheduleJsonParser()
        );

        AiScheduleVariantsResponse response = broker.requestSchedule(request);

        AiScheduleResponse variant = response.getVariant("EMPATHETIC");
        assertEquals(AiStatus.SUCCESS, variant.getStatus());
        verify(llamaAdapter).execute(request);
        verify(gemmaAdapter, never()).execute(any(AiBrokerRequest.class));
    }

    @Test
    public void requestSchedule_shouldRetryOnTransportFailures() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(2);
        properties.setRetryBackoff(Duration.ZERO);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request))
                .thenThrow(new RestClientException("first failure"))
                .thenThrow(new RestClientException("second failure"))
                .thenReturn(validJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser()
        );

        AiScheduleVariantsResponse response = broker.requestSchedule(request);

        AiScheduleResponse variant = response.getVariant("EMPATHETIC");
        assertEquals(AiStatus.SUCCESS, variant.getStatus());
        verify(gemmaAdapter, times(3)).execute(request);
    }



    @Test
    public void requestSchedule_shouldUseRateLimitBackoffPacing() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(2);
        properties.setRetryBackoff(Duration.ofMillis(100));
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request))
                .thenThrow(AiProtocolException.transportFailure("HTTP 429 Too Many Requests", null))
                .thenThrow(AiProtocolException.transportFailure("status=429", null))
                .thenReturn(validJson());

        RecordingBroker broker = new RecordingBroker(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser(),
                new AiTokenEstimator(),
                new AiTokenUsageTracker(),
                () -> 0D
        );

        AiScheduleVariantsResponse response = broker.requestSchedule(request);

        assertEquals(AiStatus.SUCCESS, response.getVariant("EMPATHETIC").getStatus());
        assertEquals(Arrays.asList(Duration.ofMillis(4000), Duration.ofMillis(8000)), broker.recordedSleeps);
        verify(gemmaAdapter, times(3)).execute(request);
    }

    @Test
    public void requestSchedule_shouldCutOffRateLimitRetriesForOversizedPayload() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(3);
        properties.setRetryBackoff(Duration.ofMillis(50));
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request))
                .thenThrow(AiProtocolException.transportFailure("HTTP 429 Too Many Requests", null));

        AiTokenEstimator oversizedEstimator = new AiTokenEstimator() {
            @Override
            public int estimateInputTokens(AiBrokerRequest request) {
                return 12000;
            }

            @Override
            public int estimateExpectedOutputTokens(AiBrokerRequest request) {
                return 200;
            }
        };

        RecordingBroker broker = new RecordingBroker(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser(),
                oversizedEstimator,
                new AiTokenUsageTracker(),
                () -> 0D
        );

        AiProtocolException exception;
        try {
            broker.requestSchedule(request);
            fail("Expected AiProtocolException");
            return;
        } catch (AiProtocolException ex) {
            exception = ex;
        }

        assertEquals(AiProtocolException.ErrorCode.TRANSPORT_FAILURE, exception.getCode());
        assertTrue(broker.recordedSleeps.isEmpty());
        verify(gemmaAdapter, times(1)).execute(request);
    }

    @Test
    public void requestSchedule_shouldSurfaceTimeouts() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ofMillis(20));

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request)).thenAnswer(invocation -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return validJson();
        });

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser()
        );

        AiProtocolException exception;
        try {
            broker.requestSchedule(request);
            fail("Expected AiProtocolException");
            return;
        } catch (AiProtocolException ex) {
            exception = ex;
        }

        assertEquals(AiProtocolException.ErrorCode.TIMEOUT, exception.getCode());
        verify(gemmaAdapter, times(1)).execute(request);
    }

    @Test
    public void requestSchedule_shouldRejectPartialSuccess() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request)).thenReturn(partialSuccessJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser()
        );

        AiProtocolException exception;
        try {
            broker.requestSchedule(request);
            fail("Expected AiProtocolException");
            return;
        } catch (AiProtocolException ex) {
            exception = ex;
        }

        assertEquals(AiProtocolException.ErrorCode.PARTIAL_SUCCESS, exception.getCode());
        verify(gemmaAdapter, times(1)).execute(request);
    }

    @Test
    public void requestSchedule_shouldRejectFailureStatus() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request)).thenReturn(failureJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser()
        );

        AiProtocolException exception;
        try {
            broker.requestSchedule(request);
            fail("Expected AiProtocolException");
            return;
        } catch (AiProtocolException ex) {
            exception = ex;
        }

        assertEquals(AiProtocolException.ErrorCode.BUSINESS_FAILURE, exception.getCode());
        verify(gemmaAdapter, times(1)).execute(request);
    }


    @Test
    public void requestSchedule_shouldRejectWhenProjectedTokenBudgetExceeded() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);

        AiTokenEstimator estimator = new AiTokenEstimator() {
            @Override
            public int estimateInputTokens(AiBrokerRequest request) {
                return 10000;
            }

            @Override
            public int estimateExpectedOutputTokens(AiBrokerRequest request) {
                return 6000;
            }
        };

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser(),
                estimator,
                new AiTokenUsageTracker(Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC")))
        );

        AiProtocolException exception;
        try {
            broker.requestSchedule(AiBrokerRequest.forToon("payload"));
            fail("Expected AiProtocolException");
            return;
        } catch (AiProtocolException ex) {
            exception = ex;
        }

        assertEquals(AiProtocolException.ErrorCode.TOKEN_BUDGET_EXCEEDED, exception.getCode());
        verify(gemmaAdapter, never()).execute(any(AiBrokerRequest.class));
    }


    @Test
    public void requestSchedule_shouldAcceptSingleVariantEnvelope() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AgentProvider.GEMMA);
        AiBrokerRequest request = AiBrokerRequest.forToon("payload");
        when(gemmaAdapter.execute(request)).thenReturn(singleVariantJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(gemmaAdapter),
                new AiScheduleJsonParser()
        );

        AiScheduleVariantsResponse response = broker.requestSchedule(request);

        assertEquals(1, response.getVariants().size());
        assertEquals(AiStatus.SUCCESS, response.getVariant("EMPATHETIC").getStatus());
    }

    @Test
    public void previewTokenBudget_shouldNotRecordUsage() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AgentProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
        AiTokenUsageTracker tracker = new AiTokenUsageTracker(clock);

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                Arrays.asList(),
                new AiScheduleJsonParser(),
                new AiTokenEstimator(),
                tracker
        );

        AiTokenBudgetGuardResult result = broker.previewTokenBudget(AiBrokerRequest.forToon("payload"));

        assertTrue(result.isAllowed());
        assertEquals(0, tracker.currentTpm(AgentProvider.GEMMA));
    }

    @Test
    public void tokenUsageTracker_shouldKeepRollingSixtySecondWindow() {
        MutableClock clock = new MutableClock(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));
        AiTokenUsageTracker tracker = new AiTokenUsageTracker(clock);

        tracker.recordUsage(AgentProvider.GEMMA, 1000, 500);
        assertEquals(1500, tracker.currentTpm(AgentProvider.GEMMA));

        clock.addSeconds(30);
        tracker.recordUsage(AgentProvider.GEMMA, 300, 200);
        assertEquals(2000, tracker.currentTpm(AgentProvider.GEMMA));

        clock.addSeconds(31);
        assertEquals(500, tracker.currentTpm(AgentProvider.GEMMA));

        int projected = tracker.projectedTpm(AgentProvider.GEMMA, 100, 150);
        assertEquals(750, projected);
        assertTrue(tracker.currentTpm(AgentProvider.LLAMA_70B) == 0);
    }

    private static String validJson() {
        return "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"EFFICIENT\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"BALANCED\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}"
                + "}"
                + "}";
    }

    private static String singleVariantJson() {
        return "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}"
                + "}"
                + "}";
    }

    private static String partialSuccessJson() {
        return "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":{"
                + "\"status\":\"PARTIAL_SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"EFFICIENT\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"BALANCED\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}"
                + "}"
                + "}";
    }

    private static String failureJson() {
        return "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":{"
                + "\"status\":\"FAILURE\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"EFFICIENT\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "},"
                + "\"BALANCED\":{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}"
                + "}"
                + "}";
    }


    private static class RecordingBroker extends AgentBrokerImpl {
        private final List<Duration> recordedSleeps = new ArrayList<>();

        private RecordingBroker(AiBrokerProperties properties,
                                List<AgentProviderAdapter> adapters,
                                AiScheduleJsonParser jsonParser,
                                AiTokenEstimator tokenEstimator,
                                AiTokenUsageTracker tokenUsageTracker,
                                DoubleSupplier jitterSource) {
            super(properties, adapters, jsonParser, tokenEstimator, tokenUsageTracker, jitterSource);
        }

        @Override
        void sleep(Duration backoff) {
            recordedSleeps.add(backoff);
        }
    }


    private static class MutableClock extends Clock {
        private Instant instant;
        private final ZoneId zoneId;

        private MutableClock(Instant instant, ZoneId zoneId) {
            this.instant = instant;
            this.zoneId = zoneId;
        }

        @Override
        public ZoneId getZone() {
            return zoneId;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new MutableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }

        private void addSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }
    }

}
