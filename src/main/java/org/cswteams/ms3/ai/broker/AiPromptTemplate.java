package org.cswteams.ms3.ai.broker;

public final class AiPromptTemplate {

    private AiPromptTemplate() {
    }

    public static String build(String instructions, String toonPayload) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are an AI scheduling agent.\n")
                .append("Use the provided TOON payload to produce a JSON response only.\n")
                .append("Do not include markdown or extra text.\n");
        if (instructions != null && !instructions.trim().isEmpty()) {
            builder.append("Instructions:\n").append(instructions.trim()).append("\n");
        }
        builder.append("TOON_INPUT:\n").append(toonPayload);
        return builder.toString();
    }
}
