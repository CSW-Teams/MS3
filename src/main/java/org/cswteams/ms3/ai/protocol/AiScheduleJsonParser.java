package org.cswteams.ms3.ai.protocol;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleVariantsResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AiScheduleJsonParser {

    private static final Logger logger = LoggerFactory.getLogger(AiScheduleJsonParser.class);
    private static final List<String> REQUIRED_VARIANT_LABELS = Collections.unmodifiableList(
            Arrays.asList("EMPATHETIC", "EFFICIENT", "BALANCED")
    );

    private final ObjectMapper objectMapper;
    private final boolean failOnTypeMismatch;

    public AiScheduleJsonParser() {
        this(true, true);
    }

    public AiScheduleJsonParser(boolean failOnUnknownProperties) {
        this(failOnUnknownProperties, true);
    }

    public AiScheduleJsonParser(boolean failOnUnknownProperties, boolean failOnTypeMismatch) {
        this.objectMapper = new ObjectMapper();
        this.failOnTypeMismatch = failOnTypeMismatch;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperties);
        this.objectMapper.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, !failOnTypeMismatch);
        if (!failOnTypeMismatch) {
            this.objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        }
    }

    public AiScheduleResponseDto parse(String json) {
        String payload = normalizeJsonPayload(json);
        if (payload == null || payload.isEmpty()) {
            throw AiProtocolException.invalidJson("Empty JSON response from AI", null);
        }
        logger.info("event=ai_json_parse_start type=single length={}", payload.length());
        try {
            AiScheduleResponseDto dto = objectMapper.readValue(payload, AiScheduleResponseDto.class);
            logger.info("event=ai_json_parse_success type=single");
            return dto;
        } catch (UnrecognizedPropertyException e) {
            throw AiProtocolException.schemaMismatch("Unknown property in AI JSON response", e);
        } catch (MismatchedInputException e) {
            if (failOnTypeMismatch) {
                String path = buildJsonPath(e);
                String detail = e.getOriginalMessage() == null ? "Type mismatch" : e.getOriginalMessage();
                throw AiProtocolException.typeMismatch("Type mismatch at " + path + ": " + detail, e);
            }
            throw AiProtocolException.schemaMismatch("Type mismatch in AI JSON response", e);
        } catch (JsonMappingException e) {
            if (failOnTypeMismatch && isTypeInstantiationFailure(e)) {
                String path = buildJsonPath(e);
                String detail = e.getOriginalMessage() == null ? "Type mismatch" : e.getOriginalMessage();
                throw AiProtocolException.typeMismatch("Type mismatch at " + path + ": " + detail, e);
            }
            throw AiProtocolException.schemaMismatch("Schema mismatch in AI JSON response", e);
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Malformed or non-JSON response from AI", e);
        }
    }

    public AiScheduleVariantsResponseDto parseVariants(String json) {
        String payload = normalizeJsonPayload(json);
        if (payload == null || payload.isEmpty()) {
            throw AiProtocolException.invalidJson("Empty JSON response from AI", null);
        }
        logger.info("event=ai_json_parse_start type=variants length={}", payload.length());
        try {
            AiScheduleVariantsResponseDto dto = objectMapper.readValue(payload, AiScheduleVariantsResponseDto.class);
            validateVariantLabels(dto);
            logger.info("event=ai_json_parse_success type=variants variants_count={}",
                    dto.variants == null ? 0 : dto.variants.size());
            return dto;
        } catch (UnrecognizedPropertyException e) {
            throw AiProtocolException.schemaMismatch("Unknown property in AI JSON response", e);
        } catch (MismatchedInputException e) {
            if (failOnTypeMismatch) {
                String path = buildJsonPath(e);
                String detail = e.getOriginalMessage() == null ? "Type mismatch" : e.getOriginalMessage();
                throw AiProtocolException.typeMismatch("Type mismatch at " + path + ": " + detail, e);
            }
            throw AiProtocolException.schemaMismatch("Type mismatch in AI JSON response", e);
        } catch (JsonMappingException e) {
            if (failOnTypeMismatch && isTypeInstantiationFailure(e)) {
                String path = buildJsonPath(e);
                String detail = e.getOriginalMessage() == null ? "Type mismatch" : e.getOriginalMessage();
                throw AiProtocolException.typeMismatch("Type mismatch at " + path + ": " + detail, e);
            }
            throw AiProtocolException.schemaMismatch("Schema mismatch in AI JSON response", e);
        } catch (IOException e) {
            throw AiProtocolException.invalidJson("Malformed or non-JSON response from AI", e);
        }
    }

    public static List<String> requiredVariantLabels() {
        return REQUIRED_VARIANT_LABELS;
    }

    private static String normalizeJsonPayload(String json) {
        if (json == null) {
            return null;
        }
        String trimmed = json.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        if (trimmed.startsWith("```")) {
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline >= 0) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
            trimmed = trimmed.trim();
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }

    private static String buildJsonPath(JsonMappingException e) {
        StringBuilder path = new StringBuilder("$");
        for (JsonMappingException.Reference ref : e.getPath()) {
            if (ref.getFieldName() != null) {
                path.append(".").append(ref.getFieldName());
            } else if (ref.getIndex() >= 0) {
                path.append("[").append(ref.getIndex()).append("]");
            }
        }
        return path.toString();
    }

    private static void validateVariantLabels(AiScheduleVariantsResponseDto dto) {
        if (dto == null || dto.variants == null || dto.variants.isEmpty()) {
            throw AiProtocolException.schemaMismatch("AI response missing variants", null);
        }

        List<String> normalizedLabels = new ArrayList<>(dto.variants.keySet());
        List<String> unexpected = new ArrayList<>();
        for (String label : normalizedLabels) {
            if (!REQUIRED_VARIANT_LABELS.contains(label)) {
                unexpected.add(label);
            }
        }
        if (!unexpected.isEmpty()) {
            throw AiProtocolException.schemaMismatch(
                    "AI response contains unexpected variants: " + String.join(", ", unexpected),
                    null
            );
        }

        if (normalizedLabels.size() > 1) {
            List<String> missing = new ArrayList<>();
            for (String requiredLabel : REQUIRED_VARIANT_LABELS) {
                if (!dto.variants.containsKey(requiredLabel)) {
                    missing.add(requiredLabel);
                }
            }
            if (!missing.isEmpty()) {
                throw AiProtocolException.schemaMismatch(
                        "AI response missing required variants: " + String.join(", ", missing),
                        null
                );
            }
        }
    }

    private static boolean isTypeInstantiationFailure(JsonMappingException e) {
        if ("com.fasterxml.jackson.databind.exc.ValueInstantiationException".equals(e.getClass().getName())) {
            return true;
        }
        Throwable cause = e.getCause();
        while (cause != null) {
            if (cause instanceof IllegalArgumentException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
