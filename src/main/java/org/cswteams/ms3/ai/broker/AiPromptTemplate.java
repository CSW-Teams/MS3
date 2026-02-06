package org.cswteams.ms3.ai.broker;

public final class AiPromptTemplate {

    private AiPromptTemplate() {
    }

    public static String build(String instructions, String toonPayload) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are an AI scheduling agent.\n")
                .append("Use the provided TOON payload to produce a JSON response only.\n")
                .append("Return a JSON object with a top-level \"variants\" field.\n")
                .append("The \"variants\" field must contain exactly three labeled schedules: \"EMPATHETIC\", \"EFFICIENT\", \"BALANCED\".\n")
                .append("Each variant must follow the schedule response schema (status, metadata, assignments, uncovered_shifts, uffa_delta).\n")
                .append("Do not include markdown or extra text.\n");
        if (instructions != null && !instructions.trim().isEmpty()) {
            builder.append("Instructions:\n").append(instructions.trim()).append("\n");
        }
        builder.append("TOON_INPUT:\n").append(toonPayload);
        return builder.toString();
    }
}
