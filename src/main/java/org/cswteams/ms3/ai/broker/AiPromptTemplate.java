package org.cswteams.ms3.ai.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cswteams.ms3.enums.Seniority;

public final class AiPromptTemplate {

    private static final Logger logger = LoggerFactory.getLogger(AiPromptTemplate.class);

    private static final String TEMPLATE_RESOURCE = "/ai/system_prompt_template.txt";
    private static final String METRICS_SPEC_ID = "DECISION_METRICS_V1";
    private static final String METRICS_SPEC_TOKEN = "${DECISION_METRICS_SPEC_ID}";
    private static final String TEMPLATE = loadTemplate();
    private static final Pattern TOON_ROLE_PATTERN = Pattern.compile("(?:^|[,\\[{])\\s*(STRUCTURED|SPECIALIST_JUNIOR|SPECIALIST_SENIOR)\\s*(?:[,\\]}]|$)");
    private AiPromptTemplate() {
    }

    public static String build(String instructions, String toonPayload) {
        return TEMPLATE + "\n\n" + buildUserContent(instructions, toonPayload);
    }

    public static String systemPrompt() {
        return TEMPLATE;
    }

    public static String metricsSpecId() {
        return METRICS_SPEC_ID;
    }

    public static String buildUserContent(String instructions, String toonPayload) {
        logPromptIntegrity(toonPayload, instructions);
        StringBuilder builder = new StringBuilder();
        if (instructions != null && !instructions.trim().isEmpty()) {
            builder.append("Instructions:\n").append(instructions.trim()).append("\n\n");
        }
        builder.append("Vocabulary rule: TOON_INPUT doctors[*].role uses the same enum naming as assignments[*].role_covered (STRUCTURED, SPECIALIST_JUNIOR, SPECIALIST_SENIOR).\n\n");
        builder.append("Holiday rule: in COMPACT mode doctor holidays use `h[n]{id,s,e,tz?}` rows (id optional, s/e inclusive dates, tz optional quoted metadata).\n\n");
        builder.append("TOON_INPUT:\n").append(toonPayload == null ? "" : toonPayload);
        return builder.toString();
    }

    private static void logPromptIntegrity(String toonPayload, String instructions) {
        String safePayload = toonPayload == null ? "" : toonPayload;
        DoctorRoleDistribution distribution = doctorRoleDistribution(safePayload);
        logger.info("event=ai_prompt_user_content_prepared toon_input_hash={} toon_input_size={} instructions_size={} role_counts={}",
                sha256Hex(safePayload),
                safePayload.length(),
                instructions == null ? 0 : instructions.length(),
                distribution.asLogMap());
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 digest is not available", ex);
        }
    }

    private static DoctorRoleDistribution doctorRoleDistribution(String toonPayload) {
        Map<Seniority, Integer> counts = new EnumMap<>(Seniority.class);
        counts.put(Seniority.STRUCTURED, 0);
        counts.put(Seniority.SPECIALIST_JUNIOR, 0);
        counts.put(Seniority.SPECIALIST_SENIOR, 0);
        Matcher matcher = TOON_ROLE_PATTERN.matcher(toonPayload);
        while (matcher.find()) {
            Seniority role = Seniority.valueOf(matcher.group(1));
            counts.put(role, counts.get(role) + 1);
        }
        return new DoctorRoleDistribution(counts);
    }

    private static final class DoctorRoleDistribution {
        private final Map<Seniority, Integer> counts;

        private DoctorRoleDistribution(Map<Seniority, Integer> counts) {
            this.counts = counts;
        }

        private Map<String, Integer> asLogMap() {
            Map<String, Integer> mapped = new java.util.LinkedHashMap<>();
            mapped.put("STRUCTURED", counts.getOrDefault(Seniority.STRUCTURED, 0));
            mapped.put("SPECIALIST_JUNIOR", counts.getOrDefault(Seniority.SPECIALIST_JUNIOR, 0));
            mapped.put("SPECIALIST_SENIOR", counts.getOrDefault(Seniority.SPECIALIST_SENIOR, 0));
            return mapped;
        }
    }

    private static String loadTemplate() {
        try (InputStream inputStream = AiPromptTemplate.class.getResourceAsStream(TEMPLATE_RESOURCE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Missing AI system prompt template resource: " + TEMPLATE_RESOURCE);
            }
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return template.replace(METRICS_SPEC_TOKEN, METRICS_SPEC_ID);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load AI system prompt template", e);
        }
    }
}
