package org.cswteams.ms3.ai.protocol;

import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.enums.Seniority;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AiScheduleSemanticValidatorTest {

    private final AiScheduleSemanticValidator validator = new AiScheduleSemanticValidator();

    @Test
    public void validate_validDto_shouldNotThrow() {
        AiScheduleResponseDto dto = validDto();
        validator.validate(dto);
    }

    @Test
    public void validate_missingReasoning_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        dto.metadata.reasoning = " ";

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.metadata.reasoning");
    }

    @Test
    public void validate_optimalityOutOfRange_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        dto.metadata.optimalityScore = 1.2;

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.metadata.optimality_score");
    }

    @Test
    public void validate_coverageOutOfRange_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        dto.metadata.metrics.coveragePercent = -0.1;

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.metadata.metrics.coverage_percent");
    }

    @Test
    public void validate_softViolationsNegative_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        dto.metadata.metrics.softViolationsCount = -1;

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.metadata.metrics.soft_violations_count");
    }

    @Test
    public void validate_forcedWithoutViolationNote_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        dto.assignments.get(0).isForced = true;
        dto.assignments.get(0).violationNote = " ";

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.assignments[0].violation_note");
    }

    @Test
    public void validate_duplicateAssignment_shouldReportError() {
        AiScheduleResponseDto dto = validDto();
        AiAssignmentDto dup = new AiAssignmentDto();
        dup.shiftId = dto.assignments.get(0).shiftId;
        dup.doctorId = dto.assignments.get(0).doctorId;
        dup.roleCovered = dto.assignments.get(0).roleCovered;
        dup.isForced = dto.assignments.get(0).isForced;
        dto.assignments.add(dup);

        AiProtocolException ex = expectSchemaMismatch(dto);
        assertHasPath(ex, "$.assignments");
    }

    private AiProtocolException expectSchemaMismatch(AiScheduleResponseDto dto) {
        try {
            validator.validate(dto);
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCategory.APPLICATION_SCHEMA, ex.getCategory());
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertNotNull(ex.getDetails());
            return ex;
        }
        return null;
    }

    private void assertHasPath(AiProtocolException ex, String path) {
        assertTrue(ex.getDetails().stream().anyMatch(d -> path.equals(d.getPath())));
    }

    private AiScheduleResponseDto validDto() {
        AiScheduleResponseDto dto = new AiScheduleResponseDto();
        dto.status = AiStatus.SUCCESS;

        AiMetadataDto metadata = new AiMetadataDto();
        metadata.reasoning = "ok";
        metadata.optimalityScore = 0.8;
        AiMetricsDto metrics = new AiMetricsDto();
        metrics.coveragePercent = 0.9;
        metrics.softViolationsCount = 0;
        metadata.metrics = metrics;
        dto.metadata = metadata;

        dto.assignments = new ArrayList<>();
        AiAssignmentDto assignment = new AiAssignmentDto();
        assignment.shiftId = "S_101_20260520";
        assignment.doctorId = 100;
        assignment.roleCovered = Seniority.STRUCTURED;
        assignment.isForced = false;
        assignment.violationNote = null;
        dto.assignments.add(assignment);
        return dto;
    }
}
