package org.cswteams.ms3.ai.broker;

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

    private static final List<String> FIXED_INSTRUCTIONS = List.of(
            "You are an AI scheduling agent.",
            "Use the provided TOON payload to produce a JSON response only.",
            "Return a JSON object with a top-level \"variants\" field.",
            "The \"variants\" field must contain exactly three labeled schedules: \"EMPATHETIC\", \"EFFICIENT\", \"BALANCED\".",
            "Each variant must follow the schedule response schema (status, metadata, assignments, uncovered_shifts, uffa_delta).",
            "Do not include markdown or extra text.",
            "Generate three schedule variants in a single JSON response.",
            "Use the labels EMPATHETIC, EFFICIENT, BALANCED under the \"variants\" object.",
            "Return only the JSON object, no extra text."
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
        when(restTemplate.postForObject(eq("http://example.test/gemma?key=token"), entityCaptor.capture(), eq(String.class)))
                .thenReturn(gemmaResponse());

        adapter.execute(request);

        HttpEntity<Map<String, Object>> entity = entityCaptor.getValue();
        Map<String, Object> payload = entity.getBody();
        assertNotNull(payload);
        Map<String, Object> systemInstruction = asMap(payload.get("system_instruction"));
        List<Map<String, Object>> systemParts = asList(systemInstruction.get("parts"));
        Map<String, Object> systemPart = systemParts.get(0);
        assertEquals(AiPromptTemplate.systemPrompt(), systemPart.get("text"));

        List<Map<String, Object>> contents = asList(payload.get("contents"));
        Map<String, Object> userContent = contents.get(0);
        assertEquals("user", userContent.get("role"));
        List<Map<String, Object>> userParts = asList(userContent.get("parts"));
        Map<String, Object> userPart = userParts.get(0);
        assertEquals(AiPromptTemplate.buildUserContent(request.getInstructions(), request.getToonPayload()), userPart.get("text"));

        String userPrompt = (String) userPart.get("text");
        assertNoFixedInstructions(userPrompt);

        String systemPrompt = (String) systemPart.get("text");
        assertSchemaConstraintsAppearOnce(systemPrompt);
    }

    private static void assertNoFixedInstructions(String userPrompt) {
        for (String instruction : FIXED_INSTRUCTIONS) {
            assertFalse("User prompt should not include fixed instruction: " + instruction,
                    userPrompt.contains(instruction));
        }
    }

    private static void assertSchemaConstraintsAppearOnce(String systemPrompt) {
        for (String instruction : FIXED_INSTRUCTIONS) {
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

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        return (Map<String, Object>) value;
    }

    private static String llamaResponse() {
        return "{\"choices\":[{\"message\":{\"content\":\"{\\\"variants\\\":{}}\"}}]}";
    }

    private static String gemmaResponse() {
        return "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"variants\\\":{}}\"}]}}]}";
    }
}
