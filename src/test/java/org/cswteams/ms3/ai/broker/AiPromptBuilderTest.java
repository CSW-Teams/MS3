package org.cswteams.ms3.ai.broker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AiPromptBuilderTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final List<String> REQUIRED_SYSTEM_KEYWORDS = List.of(
            "You are an AI scheduling agent.",
            "Return one JSON object only",
            "Top-level field required: \"variants\"",
            "must contain only allowed labels: \"EMPATHETIC\", \"EFFICIENT\", \"BALANCED\"",
            "canonical variant_schema",
            "Required fields per variant: \"status\", \"metadata\", \"assignments\", \"uncovered_shifts\", \"uffa_delta\"",
            "Field names must match exactly"
    );

    @Test
    public void llama70bPromptBuilder_shouldPlaceSystemPromptAndAvoidFixedInstructionsInUserContent() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setLlama70bUrl("http://example.test/llama");
        properties.setLlama70bApiKey("token");
        properties.setLlama70bModel("llama-70b");
        Llama70bAgentAdapter adapter = new Llama70bAgentAdapter(restTemplate, properties);

        AiBrokerRequest request = new AiBrokerRequest("toon-payload", "custom instructions", null);
        var entityCaptor = org.mockito.ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.postForObject(eq("http://example.test/llama"), entityCaptor.capture(), eq(String.class)))
                .thenReturn(llamaResponse());

        adapter.execute(request);

        HttpEntity<Map<String, Object>> entity = entityCaptor.getValue();
        Map<String, Object> payload = entity.getBody();
        assertNotNull(payload);
        List<Map<String, Object>> messages = asList(payload.get("messages"));
        Map<String, Object> systemMessage = messages.get(0);
        Map<String, Object> userMessage = messages.get(1);

        assertEquals("system", systemMessage.get("role"));
        assertEquals(AiPromptTemplate.systemPrompt(), systemMessage.get("content"));
        assertEquals("user", userMessage.get("role"));
        assertEquals(AiPromptTemplate.buildUserContent(request.getInstructions(), request.getToonPayload()), userMessage.get("content"));

        String userContent = (String) userMessage.get("content");
        assertNoFixedInstructions(userContent);

        String systemPrompt = (String) systemMessage.get("content");
        assertSchemaConstraintsAppearOnce(systemPrompt);
    }

    @Test
    public void gemmaPromptBuilder_shouldPlaceSystemPromptAndAvoidFixedInstructionsInUserContent() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setGemmaUrl("http://example.test/gemma");
        properties.setGemmaApiKey("token");
        GemmaAgentAdapter adapter = new GemmaAgentAdapter(restTemplate, properties);

        AiBrokerRequest request = new AiBrokerRequest("toon-payload", "custom instructions", null);
        var entityCaptor = org.mockito.ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.postForObject(eq("http://example.test/gemma"), entityCaptor.capture(), eq(String.class)))
                .thenReturn(gemmaResponse());

        adapter.execute(request);

        HttpEntity<String> entity = entityCaptor.getValue();
        assertEquals("token", entity.getHeaders().getFirst("x-goog-api-key"));
        String payloadBody = entity.getBody();
        assertNotNull(payloadBody);

        JsonNode payload = readTree(payloadBody);
        assertEquals("model", payload.at("/contents/0/role").asText());
        String systemPrompt = payload.at("/contents/0/parts/0/text").asText();
        assertEquals(AiPromptTemplate.systemPrompt(), systemPrompt);

        JsonNode userContent = payload.at("/contents/1");
        assertEquals("user", userContent.path("role").asText());
        String userPrompt = userContent.at("/parts/0/text").asText();
        assertEquals(AiPromptTemplate.buildUserContent(request.getInstructions(), request.getToonPayload()), userPrompt);

        assertNoFixedInstructions(userPrompt);
        assertSchemaConstraintsAppearOnce(systemPrompt);
    }

    @Test
    public void gemmaPromptBuilder_shouldSerializeQuotedInstructionsAsValidJson() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        AiBrokerProperties properties = new AiBrokerProperties();
        properties.setGemmaUrl("http://example.test/gemma");
        properties.setGemmaApiKey("token");
        GemmaAgentAdapter adapter = new GemmaAgentAdapter(restTemplate, properties);

        String instructions = "Prioritize \"night\" coverage and keep \"Dr. Smith\" in ICU";
        AiBrokerRequest request = new AiBrokerRequest("toon-payload", instructions, null);
        var entityCaptor = org.mockito.ArgumentCaptor.forClass(HttpEntity.class);
        when(restTemplate.postForObject(eq("http://example.test/gemma"), entityCaptor.capture(), eq(String.class)))
                .thenReturn(gemmaResponse());

        adapter.execute(request);

        HttpEntity<String> entity = entityCaptor.getValue();
        assertEquals("token", entity.getHeaders().getFirst("x-goog-api-key"));
        String outboundBody = entity.getBody();
        assertNotNull(outboundBody);

        JsonNode payload = readTree(outboundBody);
        String expectedUserPrompt = AiPromptTemplate.buildUserContent(instructions, request.getToonPayload());
        assertEquals("model", payload.at("/contents/0/role").asText());
        assertEquals(AiPromptTemplate.systemPrompt(), payload.at("/contents/0/parts/0/text").asText());
        assertEquals("user", payload.at("/contents/1/role").asText());
        assertEquals(expectedUserPrompt, payload.at("/contents/1/parts/0/text").asText());
    }

    private static void assertNoFixedInstructions(String userPrompt) {
        for (String instruction : REQUIRED_SYSTEM_KEYWORDS) {
            assertFalse("User prompt should not include fixed instruction: " + instruction,
                    userPrompt.contains(instruction));
        }
    }

    private static void assertSchemaConstraintsAppearOnce(String systemPrompt) {
        for (String instruction : REQUIRED_SYSTEM_KEYWORDS) {
            assertEquals("System prompt should include instruction once: " + instruction,
                    1, countOccurrences(systemPrompt, instruction));
        }
    }

    private static int countOccurrences(String source, String token) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(token, index)) >= 0) {
            count++;
            index += token.length();
        }
        return count;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> asList(Object value) {
        return (List<Map<String, Object>>) value;
    }


    private static JsonNode readTree(String payloadBody) {
        try {
            return OBJECT_MAPPER.readTree(payloadBody);
        } catch (Exception e) {
            throw new AssertionError("Expected valid JSON payload", e);
        }
    }

    private static String llamaResponse() {
        return "{\"choices\":[{\"message\":{\"content\":\"{\\\"variants\\\":{}}\"}}]}";
    }

    private static String gemmaResponse() {
        return "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"variants\\\":{}}\"}]}}]}";
    }
}
