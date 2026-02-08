package org.cswteams.ms3.ai.protocol;

import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleVariantsResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.ai.protocol.utils.AiUffaQueue;
import org.cswteams.ms3.enums.Seniority;
import org.junit.Test;

import static org.junit.Assert.*;

public class AiScheduleJsonParserTest {

    @Test
    public void parse_validJson_shouldReturnDto() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();
        String json = validJson();

        AiScheduleResponseDto dto = parser.parse(json);

        assertNotNull(dto);
        assertEquals(AiStatus.SUCCESS, dto.status);
    }

    @Test
    public void parseVariants_validJson_shouldReturnDto() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();
        String json = variantsJson(validJson());

        AiScheduleVariantsResponseDto dto = parser.parseVariants(json);

        assertNotNull(dto);
        assertEquals(3, dto.variants.size());
        assertTrue(dto.variants.containsKey("EMPATHETIC"));
        assertTrue(dto.variants.containsKey("EFFICIENT"));
        assertTrue(dto.variants.containsKey("BALANCED"));
    }

    @Test
    public void parseVariants_missingLabel_shouldThrowSchemaMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();
        String json = "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":" + validJson() + ","
                + "\"EFFICIENT\":" + validJson()
                + "}"
                + "}";

        try {
            parser.parseVariants(json);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
        }
    }

    @Test
    public void parseVariants_unexpectedLabel_shouldThrowSchemaMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();
        String json = "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":" + validJson() + ","
                + "\"EFFICIENT\":" + validJson() + ","
                + "\"BALANCED\":" + validJson() + ","
                + "\"EXTRA\":" + validJson()
                + "}"
                + "}";

        try {
            parser.parseVariants(json);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
        }
    }

    @Test(expected = AiProtocolException.class)
    public void parse_malformedJson_shouldThrow() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();
        parser.parse("{ not a json }");
    }

    @Test
    public void parse_malformedJson_shouldThrowInvalidJson() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser();

        try {
            parser.parse("{ not a json }");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.INVALID_JSON, ex.getCode());
        }
    }

    @Test
    public void parse_unknownProperty_withStrictMode_shouldThrowSchemaMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{\"metrics\":{\"unexpected\":123}},"
                + "\"assignments\":[{\"unexpected\":123}],"
                + "\"unexpected_root\":123"
                + "}";

        try {
            parser.parse(json);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
        }
    }

    @Test
    public void parse_unknownProperty_withNonStrictMode_shouldParse() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(false);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{\"metrics\":{\"unexpected\":123}},"
                + "\"assignments\":[{\"unexpected\":123}],"
                + "\"unexpected_root\":123"
                + "}";

        AiScheduleResponseDto dto = parser.parse(json);

        assertNotNull(dto);
        assertEquals(AiStatus.SUCCESS, dto.status);
    }

    @Test
    public void parse_typeMismatch_assignmentsObject_shouldThrowTypeMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":{},"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        assertTypeMismatch(parser, json, "$.assignments");
    }

    @Test
    public void parse_typeMismatch_doctorIdString_shouldThrowTypeMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":\"100\",\"role_covered\":\"STRUCTURED\",\"is_forced\":false}],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        assertTypeMismatch(parser, json, "$.assignments[0].doctor_id");
    }

    @Test
    public void parse_typeMismatch_isForcedString_shouldThrowTypeMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":100,\"role_covered\":\"STRUCTURED\",\"is_forced\":\"false\"}],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        assertTypeMismatch(parser, json, "$.assignments[0].is_forced");
    }

    @Test
    public void parse_typeMismatch_metadataArray_shouldThrowTypeMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":[],"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":100,\"role_covered\":\"STRUCTURED\",\"is_forced\":false}],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        assertTypeMismatch(parser, json, "$.metadata");
    }

    @Test
    public void parse_typeMismatch_doctorIdString_withPermissiveMode_shouldParse() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, false);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":\"100\",\"role_covered\":\"STRUCTURED\",\"is_forced\":false}],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        AiScheduleResponseDto dto = parser.parse(json);

        assertNotNull(dto);
        assertEquals(AiStatus.SUCCESS, dto.status);
        assertNotNull(dto.assignments);
        assertEquals(Integer.valueOf(100), dto.assignments.get(0).doctorId);
    }

    @Test
    public void parse_fullJson_shouldMapAllFields() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":100,\"role_covered\":\"STRUCTURED\",\"is_forced\":false,\"violation_note\":\"note\"}],"
                + "\"uncovered_shifts\":[{\"shift_id\":\"S_105_20260521\",\"reason\":\"missing\"}],"
                + "\"uffa_delta\":[{\"doctor_id\":100,\"queue\":\"gen\",\"points\":5}]"
                + "}";

        AiScheduleResponseDto dto = parser.parse(json);

        assertEquals(AiStatus.SUCCESS, dto.status);
        assertNotNull(dto.metadata);
        assertNotNull(dto.metadata.metrics);
        assertNotNull(dto.metadata.metrics.uffaBalance);
        assertNotNull(dto.metadata.metrics.uffaBalance.nightShiftStdDev);
        assertEquals(Double.valueOf(22.5), dto.metadata.metrics.uffaBalance.nightShiftStdDev.finalValue);
        assertEquals(Seniority.STRUCTURED, dto.assignments.get(0).roleCovered);
        assertEquals(AiUffaQueue.GEN, dto.uffaDelta.get(0).queue);
    }

    @Test
    public void parse_invalidEnumStatus_shouldThrowTypeMismatch() {
        AiScheduleJsonParser parser = new AiScheduleJsonParser(true, true);
        String json = "{"
                + "\"status\":\"OK\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";

        try {
            parser.parse(json);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.TYPE_MISMATCH, ex.getCode());
            assertTrue(ex.getMessage().contains("$.status"));
        }
    }

    private static void assertTypeMismatch(AiScheduleJsonParser parser, String json, String expectedPath) {
        try {
            parser.parse(json);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.TYPE_MISMATCH, ex.getCode());
            assertTrue(ex.getMessage().contains(expectedPath));
        }
    }

    private static String variantsJson(String variantJson) {
        return "{"
                + "\"variants\":{"
                + "\"EMPATHETIC\":" + variantJson + ","
                + "\"EFFICIENT\":" + variantJson + ","
                + "\"BALANCED\":" + variantJson
                + "}"
                + "}";
    }

    private static String validJson() {
        return "{"
                + "\"status\":\"SUCCESS\","
                + "\"metadata\":{"
                + "\"reasoning\":\"ok\","
                + "\"optimality_score\":0.85,"
                + "\"metrics\":{"
                + "\"coverage_percent\":0.98,"
                + "\"uffa_balance\":{\"night_shift_std_dev\":{\"initial\":40.1,\"final\":22.5}},"
                + "\"soft_violations_count\":1"
                + "}"
                + "},"
                + "\"assignments\":[{\"shift_id\":\"S_101_20260520\",\"doctor_id\":100,\"role_covered\":\"STRUCTURED\",\"is_forced\":false}],"
                + "\"uncovered_shifts\":[],"
                + "\"uffa_delta\":[]"
                + "}";
    }

}
