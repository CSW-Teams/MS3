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

public class Llama70bAgentAdapter implements AgentProviderAdapter {

    private final RestTemplate restTemplate;
    private final AiBrokerProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Llama70bAgentAdapter(RestTemplate restTemplate, AiBrokerProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public AgentProvider provider() {
        return AgentProvider.LLAMA_70B;
    }

    @Override
    public String execute(AiBrokerRequest request) {
        String url = properties.getLlama70bUrl();
        if (url == null || url.isBlank()) {
            throw AiProtocolException.businessFailure("Llama-70B URL is not configured");
        }
        String apiKey = properties.getLlama70bApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw AiProtocolException.businessFailure("Llama-70B API key is not configured");
        }
        String model = properties.getLlama70bModel();
        if (model == null || model.isBlank()) {
            throw AiProtocolException.businessFailure("Llama-70B model is not configured");
        }

        String systemPrompt = AiPromptTemplate.systemPrompt();
        String userPrompt = AiPromptTemplate.buildUserContent(request.getInstructions(), request.getToonPayload());
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "response_format", Map.of("type", "json_object")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        String response = restTemplate.postForObject(url, entity, String.class);
        if (response == null || response.isBlank()) {
            throw AiProtocolException.invalidJson("Empty response from Llama-70B API", null);
        }
        return extractJsonPayload(response);
    }

    private String extractJsonPayload(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode content = root.at("/choices/0/message/content");
            if (content.isMissingNode() || content.isNull()) {
                throw AiProtocolException.schemaMismatch("Llama-70B response missing choices content", null);
            }
            return content.asText();
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Llama-70B response is not valid JSON", e);
        }
    }
}
