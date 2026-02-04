package org.cswteams.ms3.ai.broker;

import org.springframework.web.client.RestTemplate;

public class GemmaAgentAdapter implements AgentProviderAdapter {
    private final RestTemplate restTemplate;
    private final AiBrokerProperties properties;

    public GemmaAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public AiProvider provider() {
        return AiProvider.GEMMA;
    }

    @Override
    public String requestSchedule(String requestPayload) {
        String url = properties.getGemmaUrl();
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("Gemma URL is not configured.");
        }
        String response = restTemplate.postForObject(url, requestPayload, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Empty response from Gemma provider.");
        }
        return response;
    }
}
