package org.cswteams.ms3.ai.protocol;

import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AiScheduleWellKnownJsonValidationTest {

    private static final Path WELL_KNOWN_SIMULATED_SCHEDULE_PATH =
            Paths.get("src", "test", "resources", "ai", "simulated-schedule.json");

    private final AiScheduleJsonParser parser = new AiScheduleJsonParser();
    private final AiScheduleSemanticValidator validator = new AiScheduleSemanticValidator();

    @Test
    public void simulatedScheduleJson_fromWellKnownLocation_shouldDeserializeAndValidate() throws IOException {
        assertFalse("Expected simulated schedule JSON file at well-known path: " + WELL_KNOWN_SIMULATED_SCHEDULE_PATH,
                Files.notExists(WELL_KNOWN_SIMULATED_SCHEDULE_PATH));

        String json = new String(Files.readAllBytes(WELL_KNOWN_SIMULATED_SCHEDULE_PATH), StandardCharsets.UTF_8);
        AiScheduleResponseDto dto = parser.parse(json);

        assertNotNull(dto);
        validator.validate(dto);
    }
}
