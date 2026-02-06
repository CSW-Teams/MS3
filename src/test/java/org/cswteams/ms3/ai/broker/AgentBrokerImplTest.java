package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        AiScheduleResponse response = broker.requestSchedule(request);

        assertEquals(AiStatus.SUCCESS, response.getStatus());
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

        AiScheduleResponse response = broker.requestSchedule(request);

        assertEquals(AiStatus.SUCCESS, response.getStatus());
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

        AiProtocolException exception = assertThrows(
                AiProtocolException.class,
                () -> broker.requestSchedule(request)
        );

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

        AiProtocolException exception = assertThrows(
                AiProtocolException.class,
                () -> broker.requestSchedule(request)
        );

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

        AiProtocolException exception = assertThrows(
                AiProtocolException.class,
                () -> broker.requestSchedule(request)
        );

        assertEquals(AiProtocolException.ErrorCode.BUSINESS_FAILURE, exception.getCode());
        verify(gemmaAdapter, times(1)).execute(request);
    }

    private static String validJson() {
        return "{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";
    }

    private static String partialSuccessJson() {
        return "{"
                + "\"status\":\"PARTIAL_SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";
    }

    private static String failureJson() {
        return "{"
                + "\"status\":\"FAILURE\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";
    }
}
