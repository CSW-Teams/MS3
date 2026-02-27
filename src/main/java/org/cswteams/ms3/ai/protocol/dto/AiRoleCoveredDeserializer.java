package org.cswteams.ms3.ai.protocol.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.cswteams.ms3.enums.Seniority;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AiRoleCoveredDeserializer extends JsonDeserializer<Seniority> {

    private static final Map<String, Seniority> ROLE_MAPPINGS;

    static {
        Map<String, Seniority> roleMappings = new LinkedHashMap<>();
        roleMappings.put("STRUCTURED", Seniority.STRUCTURED);
        roleMappings.put("SPECIALIST_JUNIOR", Seniority.SPECIALIST_JUNIOR);
        roleMappings.put("SPECIALIST_SENIOR", Seniority.SPECIALIST_SENIOR);
        roleMappings.put("JUNIOR", Seniority.SPECIALIST_JUNIOR);
        ROLE_MAPPINGS = Collections.unmodifiableMap(roleMappings);
    }

    @Override
    public Seniority deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }

        if (!parser.currentToken().isScalarValue()) {
            throw InvalidFormatException.from(
                    parser,
                    "Unsupported role_covered value type. Expected one of: STRUCTURED, SPECIALIST_JUNIOR, SPECIALIST_SENIOR, JUNIOR",
                    parser.getText(),
                    Seniority.class
            );
        }

        String rawValue = parser.getValueAsString();
        String normalizedValue = rawValue == null ? null : rawValue.trim().toUpperCase(Locale.ROOT);
        Seniority mapped = ROLE_MAPPINGS.get(normalizedValue);
        if (mapped != null) {
            return mapped;
        }

        throw InvalidFormatException.from(
                parser,
                "Unsupported role_covered value '" + rawValue + "'. Supported values: STRUCTURED, SPECIALIST_JUNIOR, SPECIALIST_SENIOR (alias: JUNIOR)",
                rawValue,
                Seniority.class
        );
    }
}
