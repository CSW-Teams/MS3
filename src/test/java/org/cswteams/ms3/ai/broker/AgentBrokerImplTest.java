package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
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
}
