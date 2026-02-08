package org.cswteams.ms3.ai.broker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GemmaAgentAdapter implements AgentProviderAdapter {

    private final RestTemplate restTemplate;
    private final AiBrokerProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GemmaAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public AgentProvider provider() {
        return AgentProvider.GEMMA;
    }

    @Override
    public String execute(AiBrokerRequest request) {
        String url = properties.getGemmaUrl();
        if (url == null || url.isBlank()) {
            throw AiProtocolException.businessFailure("Gemma URL is not configured");
        }
        String apiKey = properties.getGemmaApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw AiProtocolException.businessFailure("Gemma API key is not configured");
        }

        String prompt = AiPromptTemplate.build(request.getInstructions(), request.getToonPayload());
        Map<String, Object> payload = Map.of("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt))))
                //,"generationConfig", Map.of("responseMimeType", "application/json")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        url = String.format("%s?key=%s", url, apiKey);

        String response = restTemplate.postForObject(url, entity, String.class);
        if (response == null || response.isBlank()) {
            throw AiProtocolException.invalidJson("Empty response from Gemma API", null);
        }
        return extractJsonPayload(response);
    }

    private String extractJsonPayload(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.at("/candidates/0/content/parts/0/text");
            if (content.isMissingNode() || content.isNull()) {
                throw AiProtocolException.schemaMismatch("Gemma response missing candidates content", null);
            }
            return content.asText();
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Gemma response is not valid JSON", e);
        }
    }
}
