package org.cswteams.ms3.ai.protocol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.cswteams.ms3.ai.protocol.dto.AiUffaDeltaDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AiUffaDeltaDeserializer extends JsonDeserializer<List<AiUffaDeltaDto>> {

    private static final Logger logger = LoggerFactory.getLogger(AiUffaDeltaDeserializer.class);

    @Override
    public List<AiUffaDeltaDto> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (token == null) {
            token = parser.nextToken();
        }

        if (token == JsonToken.START_ARRAY) {
            JavaType type = context.getTypeFactory()
                    .constructCollectionType(List.class, AiUffaDeltaDto.class);
            return context.readValue(parser, type);
        }

        if (token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT) {
            Number value = parser.getNumberValue();
            logger.warn("event=ai_schema_drift field=uffa_delta expected=array actual=number value={}", value);
            return Collections.emptyList();
        }

        return context.reportInputMismatch(
                List.class,
                "Expected array or number for uffa_delta but got %s",
                token
        );
    }
}
