package org.cswteams.ms3.ai.broker;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.junit.Test;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AgentBrokerImplTest {

    @Test
    public void requestSchedule_shouldSelectConfiguredProvider() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AiProvider.LLAMA70B);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AiProvider.GEMMA);

        AgentProviderAdapter llamaAdapter = mock(AgentProviderAdapter.class);
        when(llamaAdapter.provider()).thenReturn(AiProvider.LLAMA70B);
        when(llamaAdapter.requestSchedule("payload")).thenReturn(validJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                new AiScheduleJsonParser(),
                Arrays.asList(gemmaAdapter, llamaAdapter)
        );

        AiScheduleResponseDto response = broker.requestSchedule("payload");

        assertEquals(AiStatus.SUCCESS, response.status);
        verify(llamaAdapter).requestSchedule("payload");
        verify(gemmaAdapter, never()).requestSchedule(anyString());
    }

    @Test
    public void requestSchedule_shouldRetryOnTransportFailures() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AiProvider.GEMMA);
        properties.setMaxRetries(2);
        properties.setRetryBackoff(Duration.ZERO);
        properties.setTotalTimeout(Duration.ZERO);

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AiProvider.GEMMA);
        when(gemmaAdapter.requestSchedule("payload"))
                .thenThrow(new RestClientException("first failure"))
                .thenThrow(new RestClientException("second failure"))
                .thenReturn(validJson());

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                new AiScheduleJsonParser(),
                Arrays.asList(gemmaAdapter)
        );

        AiScheduleResponseDto response = broker.requestSchedule("payload");

        assertEquals(AiStatus.SUCCESS, response.status);
        verify(gemmaAdapter, times(3)).requestSchedule("payload");
    }

    @Test
    public void requestSchedule_shouldSurfaceTimeouts() {
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setProvider(AiProvider.GEMMA);
        properties.setMaxRetries(0);
        properties.setTotalTimeout(Duration.ofMillis(20));

        AgentProviderAdapter gemmaAdapter = mock(AgentProviderAdapter.class);
        when(gemmaAdapter.provider()).thenReturn(AiProvider.GEMMA);
        when(gemmaAdapter.requestSchedule("payload")).thenAnswer(invocation -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return validJson();
        });

        AgentBrokerImpl broker = new AgentBrokerImpl(
                properties,
                new AiScheduleJsonParser(),
                Arrays.asList(gemmaAdapter)
        );

        AiProtocolException exception = assertThrows(
                AiProtocolException.class,
                () -> broker.requestSchedule("payload")
        );

        assertEquals(AiProtocolException.ErrorCode.TIMEOUT, exception.getCode());
        verify(gemmaAdapter, times(1)).requestSchedule("payload");
    }

    private static String validJson() {
        return "{"
                + "\"status\":\"SUCCESS\","
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";
    }
}
