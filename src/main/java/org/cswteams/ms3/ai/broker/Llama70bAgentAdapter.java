package org.cswteams.ms3.ai.broker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
        String payloadJson = buildPayloadJson(model, systemPrompt, userPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

        String response = restTemplate.postForObject(url, entity, String.class);
        if (response == null || response.isBlank()) {
            throw AiProtocolException.invalidJson("Empty response from Llama-70B API", null);
        }
        return extractJsonPayload(response);
    }

    private String buildPayloadJson(String model, String systemPrompt, String userPrompt) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("model", model);
        ArrayNode messages = payload.putArray("messages");

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", userPrompt);

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Llama-70B request payload is not valid JSON", e);
        }
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
