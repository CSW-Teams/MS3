package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class AiScheduleVariantsResponseDto {

    private static final Set<String> ALLOWED_VARIANT_LABELS = Collections.unmodifiableSet(
            new LinkedHashSet<>(Arrays.asList("EMPATHETIC", "EFFICIENT", "BALANCED"))
    );

    @JsonProperty("variants")
    @JsonDeserialize(using = VariantsDeserializer.class)
    public Map<String, AiScheduleResponseDto> variants = new LinkedHashMap<>();

    public static class VariantsDeserializer extends JsonDeserializer<Map<String, AiScheduleResponseDto>> {
        @Override
        public Map<String, AiScheduleResponseDto> deserialize(JsonParser parser,
                                                              DeserializationContext context) throws IOException {
            ObjectCodec codec = parser.getCodec();
            JsonNode variantsNode = codec.readTree(parser);

            if (variantsNode == null || variantsNode.isNull()) {
                return null;
            }

            if (variantsNode.isObject()) {
                return deserializeFromObject(codec, variantsNode);
            }

            if (variantsNode.isArray()) {
                return deserializeFromArray(codec, variantsNode);
            }

            throw AiProtocolException.schemaMismatch("AI response variants must be an object or array", null);
        }

        private static Map<String, AiScheduleResponseDto> deserializeFromObject(ObjectCodec codec, JsonNode variantsNode)
                throws IOException {
            Map<String, AiScheduleResponseDto> normalized = new LinkedHashMap<>();
            variantsNode.fields().forEachRemaining(entry -> {
                String label = entry.getKey();
                validateAllowedLabel(label);
                if (normalized.containsKey(label)) {
                    throw AiProtocolException.schemaMismatch("AI response contains duplicate variant label: " + label, null);
                }
                try {
                    normalized.put(label, codec.treeToValue(entry.getValue(), AiScheduleResponseDto.class));
                } catch (IOException e) {
                    throw AiProtocolException.schemaMismatch("AI response variant " + label + " cannot be deserialized", e);
                }
            });
            return normalized;
        }

        private static Map<String, AiScheduleResponseDto> deserializeFromArray(ObjectCodec codec, JsonNode variantsNode)
                throws IOException {
            Map<String, AiScheduleResponseDto> normalized = new LinkedHashMap<>();
            for (int index = 0; index < variantsNode.size(); index++) {
                JsonNode itemNode = variantsNode.get(index);
                if (itemNode == null || !itemNode.isObject()) {
                    throw AiProtocolException.schemaMismatch(
                            "AI response variant at index " + index + " must be an object",
                            null
                    );
                }

                JsonNode labelNode = itemNode.get("label");
                if (labelNode == null || !labelNode.isTextual() || labelNode.asText().trim().isEmpty()) {
                    throw AiProtocolException.schemaMismatch(
                            "AI response variant at index " + index + " is missing label",
                            null
                    );
                }
                String label = labelNode.asText().trim();
                validateAllowedLabel(label);
                if (normalized.containsKey(label)) {
                    throw AiProtocolException.schemaMismatch("AI response contains duplicate variant label: " + label, null);
                }

                JsonNode payloadNode = itemNode.get("variant");
                if (payloadNode == null || payloadNode.isNull()) {
                    ObjectNode payloadObject = itemNode.deepCopy();
                    payloadObject.remove("label");
                    payloadNode = payloadObject;
                }

                try {
                    normalized.put(label, codec.treeToValue(payloadNode, AiScheduleResponseDto.class));
                } catch (IOException e) {
                    throw AiProtocolException.schemaMismatch("AI response variant " + label + " cannot be deserialized", e);
                }
            }
            return normalized;
        }

        private static void validateAllowedLabel(String label) {
            if (!ALLOWED_VARIANT_LABELS.contains(label)) {
                throw AiProtocolException.schemaMismatch("AI response contains unexpected variant " + label, null);
            }
        }
    }
}
