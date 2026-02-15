package org.cswteams.ms3.ai.protocol.converter;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.AiScheduleSemanticValidator;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AiScheduleConverterServiceTest {

    @Test
    public void convert_missingRequiredDoctorsBySeniority_shouldFailWithSchemaMismatch() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        dto.assignments.add(assignment("S_101_20260520", 1, Seniority.STRUCTURED));

        Doctor structuredDoctor = new Doctor(
                "Mario",
                "Rossi",
                "RSSMRA80A01H501U",
                LocalDate.of(1980, 1, 1),
                "mario.rossi@example.com",
                "pwd",
                Seniority.STRUCTURED,
                Set.of(SystemActor.USER)
        );

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(Map.of(Seniority.STRUCTURED, 2), new Task(TaskEnum.WARD));
        when(shiftTemplate.getQuantityShiftSeniority()).thenReturn(List.of(qss));

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findById(anyLong())).thenReturn(structuredDoctor);
        when(shiftDAO.findById(101L)).thenReturn(Optional.of(shiftTemplate));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        try {
            service.convert("payload");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertTrue(ex.getDetails().stream().anyMatch(d -> "$.assignments".equals(d.getPath())));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("requires at least 2 doctors with seniority=STRUCTURED")));
        }
    }

    @Test
    public void convert_roleCoveredDifferentFromDoctorSeniority_shouldFailWithSchemaMismatch() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        dto.assignments.add(assignment("S_101_20260520", 1, Seniority.SPECIALIST_SENIOR));

        Doctor structuredDoctor = new Doctor(
                "Mario",
                "Rossi",
                "RSSMRA80A01H501U",
                LocalDate.of(1980, 1, 1),
                "mario.rossi@example.com",
                "pwd",
                Seniority.STRUCTURED,
                Set.of(SystemActor.USER)
        );

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(Map.of(Seniority.SPECIALIST_SENIOR, 1), new Task(TaskEnum.WARD));
        when(shiftTemplate.getQuantityShiftSeniority()).thenReturn(List.of(qss));

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findById(anyLong())).thenReturn(structuredDoctor);
        when(shiftDAO.findById(101L)).thenReturn(Optional.of(shiftTemplate));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        try {
            service.convert("payload");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertTrue(ex.getDetails().stream().anyMatch(d -> "$.assignments[0].role_covered".equals(d.getPath())));
        }
    }

    private AiScheduleResponseDto validDto() {
        AiScheduleResponseDto dto = new AiScheduleResponseDto();
        dto.status = AiStatus.SUCCESS;

        AiMetadataDto metadata = new AiMetadataDto();
        metadata.reasoning = "ok";
        metadata.optimalityScore = 0.8;
        AiMetricsDto metrics = new AiMetricsDto();
        metrics.coveragePercent = 90.0;
        metrics.softViolationsCount = 0;
        metadata.metrics = metrics;
        dto.metadata = metadata;

        dto.assignments = new ArrayList<>();
        return dto;
    }

    private AiAssignmentDto assignment(String shiftId, Integer doctorId, Seniority roleCovered) {
        AiAssignmentDto dto = new AiAssignmentDto();
        dto.shiftId = shiftId;
        dto.doctorId = doctorId;
        dto.roleCovered = roleCovered;
        dto.isForced = false;
        dto.violationNote = null;
        return dto;
    }
}
