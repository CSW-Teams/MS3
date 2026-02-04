package org.cswteams.ms3.ai.broker;

import org.springframework.web.client.RestTemplate;

public class Llama70bAgentAdapter implements AgentProviderAdapter {
    private final RestTemplate restTemplate;
    private final AiBrokerProperties properties;

    public Llama70bAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public AiProvider provider() {
        return AiProvider.LLAMA70B;
    }

    @Override
    public String requestSchedule(String requestPayload) {
        String url = properties.getLlama70bUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Llama 70B URL is not configured.");
        }
        String response = restTemplate.postForObject(url, requestPayload, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Empty response from Llama 70B provider.");
        }
        return response;
    }
}
