package org.cswteams.ms3.ai.broker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
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

        String systemPrompt = AiPromptTemplate.systemPrompt();
        String userPrompt = AiPromptTemplate.buildUserContent(request.getInstructions(), request.getToonPayload());
        String payloadJson = buildPayloadJson(systemPrompt, userPrompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);
        HttpEntity<String> entity = new HttpEntity<>(payloadJson, headers);

        log.info("Sending request to Gemma: {}", payloadJson);

        String response = restTemplate.postForObject(url, entity, String.class);
        if (response == null || response.isBlank()) {
            throw AiProtocolException.invalidJson("Empty response from Gemma API", null);
        }
        return extractJsonPayload(response);
    }

    private String buildPayloadJson(String systemPrompt, String userPrompt) {
        ObjectNode payload = objectMapper.createObjectNode();
        ArrayNode contents = payload.putArray("contents");
        ObjectNode systemContent = contents.addObject();
        systemContent.put("role", "model");
        ArrayNode systemParts = systemContent.putArray("parts");
        systemParts.addObject().put("text", systemPrompt);

        ObjectNode userContent = contents.addObject();
        userContent.put("role", "user");
        ArrayNode userParts = userContent.putArray("parts");
        userParts.addObject().put("text", userPrompt);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Gemma request payload is not valid JSON", e);
        }
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
