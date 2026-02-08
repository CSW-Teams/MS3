package org.cswteams.ms3.ai.broker;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class AiPromptTemplate {

    private static final String TEMPLATE_RESOURCE = "/ai/system_prompt_template.txt";
    private static final String TEMPLATE = loadTemplate();
    private static final String VARIANT_LABELS = "EMPATHETIC, EFFICIENT, BALANCED";
    private static final String EMPATHETIC_INTENT =
            "Maximize doctor well-being and respect expressed preferences as strict constraints.";
    private static final String EFFICIENT_INTENT =
            "Optimize coverage and fairness metrics, minimizing uncovered shifts and priority variance.";
    private static final String BALANCED_INTENT =
            "Balance well-being and operational efficiency, allowing soft constraint tradeoffs.";

    private AiPromptTemplate() {
    }

    public static String build(String instructions, String toonPayload) {
        return TEMPLATE + "\n\n" + buildUserContent(instructions, toonPayload);
    }

    public static String systemPrompt() {
        return TEMPLATE;
    }

    public static String buildUserContent(String instructions, String toonPayload) {
        StringBuilder builder = new StringBuilder();
        if (instructions != null && !instructions.trim().isEmpty()) {
            builder.append("Instructions:\n").append(instructions.trim()).append("\n\n");
        }
        builder.append("TOON_INPUT:\n").append(toonPayload == null ? "" : toonPayload);
        return builder.toString();
    }

    public static String buildVariantIntentInstructions() {
        StringBuilder builder = new StringBuilder();
        builder.append("- EMPATHETIC: ").append(EMPATHETIC_INTENT).append("\n");
        builder.append("- EFFICIENT: ").append(EFFICIENT_INTENT).append("\n");
        builder.append("- BALANCED: ").append(BALANCED_INTENT).append("\n");
        return builder.toString();
    }

    public static String variantLabels() {
        return VARIANT_LABELS;
    }

    private static String loadTemplate() {
        try (InputStream inputStream = AiPromptTemplate.class.getResourceAsStream(TEMPLATE_RESOURCE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing AI system prompt template resource: " + TEMPLATE_RESOURCE);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load AI system prompt template", e);
        }
    }
}
