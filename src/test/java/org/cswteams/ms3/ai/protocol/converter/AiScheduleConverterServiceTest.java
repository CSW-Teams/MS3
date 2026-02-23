package org.cswteams.ms3.ai.protocol.converter;

import org.cswteams.ms3.ai.protocol.AiScheduleJsonParser;
import org.cswteams.ms3.ai.protocol.AiScheduleSemanticValidator;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiRoleValidationScratchpadItemDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
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
                Set.of(SystemActor.DOCTOR)
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
                Set.of(SystemActor.DOCTOR)
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


    @Test
    public void convert_missingOnCallLayer_shouldFailWithStatusSpecificCoverageError() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        AiAssignmentDto onDuty = assignment("S_101_20260520", 1, Seniority.STRUCTURED);
        onDuty.assignmentStatus = ConcreteShiftDoctorStatus.ON_DUTY;
        dto.assignments.add(onDuty);

        Doctor structuredDoctor = new Doctor(
                "Mario",
                "Rossi",
                "RSSMRA80A01H501U",
                LocalDate.of(1980, 1, 1),
                "mario.rossi@example.com",
                "pwd",
                Seniority.STRUCTURED,
                Set.of(SystemActor.DOCTOR)
        );

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(Map.of(Seniority.STRUCTURED, 1), new Task(TaskEnum.WARD));
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
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("assignment_status=ON_CALL")));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("seniority=STRUCTURED")));
        }
    }

    @Test
    public void convert_onDutyAndOnCallBothSatisfyMinima_shouldPass() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 1, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 2, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 3, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 4, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL));

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findById(1L)).thenReturn(doctor(1L, Seniority.STRUCTURED));
        when(doctorDAO.findById(2L)).thenReturn(doctor(2L, Seniority.SPECIALIST_JUNIOR));
        when(doctorDAO.findById(3L)).thenReturn(doctor(3L, Seniority.STRUCTURED));
        when(doctorDAO.findById(4L)).thenReturn(doctor(4L, Seniority.SPECIALIST_JUNIOR));

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1),
                new Task(TaskEnum.WARD)
        );
        when(shiftTemplate.getQuantityShiftSeniority()).thenReturn(List.of(qss));
        when(shiftDAO.findById(101L)).thenReturn(Optional.of(shiftTemplate));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        List<ConcreteShift> converted = service.convert("payload");

        assertEquals(1, converted.size());
        assertEquals(4, converted.get(0).getDoctorAssignmentList().size());
    }

    @Test
    public void convert_onDutySatisfiedButOnCallInsufficient_shouldFailWithOnCallContext() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 1, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 2, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 3, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL));

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findById(1L)).thenReturn(doctor(1L, Seniority.STRUCTURED));
        when(doctorDAO.findById(2L)).thenReturn(doctor(2L, Seniority.SPECIALIST_JUNIOR));
        when(doctorDAO.findById(3L)).thenReturn(doctor(3L, Seniority.STRUCTURED));

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1),
                new Task(TaskEnum.WARD)
        );
        when(shiftTemplate.getQuantityShiftSeniority()).thenReturn(List.of(qss));
        when(shiftDAO.findById(101L)).thenReturn(Optional.of(shiftTemplate));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        try {
            service.convert("payload");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("shift_id=S_101_20260520")));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("assignment_status=ON_CALL")));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("seniority=SPECIALIST_JUNIOR")));
        }
    }

    @Test
    public void convert_onCallSatisfiedButOnDutyInsufficient_shouldFailWithOnDutyContext() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 1, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 3, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL));
        dto.assignments.add(assignmentWithStatus("S_101_20260520", 4, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL));

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findById(1L)).thenReturn(doctor(1L, Seniority.STRUCTURED));
        when(doctorDAO.findById(3L)).thenReturn(doctor(3L, Seniority.STRUCTURED));
        when(doctorDAO.findById(4L)).thenReturn(doctor(4L, Seniority.SPECIALIST_JUNIOR));

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1),
                new Task(TaskEnum.WARD)
        );
        when(shiftTemplate.getQuantityShiftSeniority()).thenReturn(List.of(qss));
        when(shiftDAO.findById(101L)).thenReturn(Optional.of(shiftTemplate));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        try {
            service.convert("payload");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("shift_id=S_101_20260520")));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("assignment_status=ON_DUTY")));
            assertTrue(ex.getDetails().stream().anyMatch(d -> d.getMessage().contains("seniority=SPECIALIST_JUNIOR")));
        }
    }


    @Test
    public void convert_missingAssignmentStatus_shouldFailFast() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        dto.assignments = new ArrayList<>();
        AiAssignmentDto assignment = assignment("S_101_20260520", 1, Seniority.STRUCTURED);
        assignment.assignmentStatus = null;
        dto.assignments.add(assignment);

        Doctor structuredDoctor = new Doctor(
                "Mario",
                "Rossi",
                "RSSMRA80A01H501U",
                LocalDate.of(1980, 1, 1),
                "mario.rossi@example.com",
                "pwd",
                Seniority.STRUCTURED,
                Set.of(SystemActor.DOCTOR)
        );

        Shift shiftTemplate = mock(Shift.class);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(Map.of(Seniority.STRUCTURED, 1), new Task(TaskEnum.WARD));
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
            assertEquals(AiProtocolException.ErrorCode.INVALID_FORMAT, ex.getCode());
            assertTrue(ex.getMessage().contains("Missing assignment_status"));
        }
    }

    @Test
    public void convert_scratchpadCandidateWrongRole_shouldFailFastWithSchemaMismatch() {
        AiScheduleJsonParser jsonParser = mock(AiScheduleJsonParser.class);
        AiScheduleSemanticValidator semanticValidator = mock(AiScheduleSemanticValidator.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        ShiftDAO shiftDAO = mock(ShiftDAO.class);

        AiScheduleResponseDto dto = validDto();
        AiRoleValidationScratchpadItemDto scratchpadItem = new AiRoleValidationScratchpadItemDto();
        scratchpadItem.shiftId = "S_101_20260520";
        scratchpadItem.roleRequired = "STRUCTURED";
        scratchpadItem.candidateDoctorIds.add(99);
        dto.metadata.roleValidationScratchpad.add(scratchpadItem);

        Doctor seniorDoctor = mock(Doctor.class);
        when(seniorDoctor.getId()).thenReturn(99L);
        when(seniorDoctor.getSeniority()).thenReturn(Seniority.SPECIALIST_SENIOR);

        when(jsonParser.parse("payload")).thenReturn(dto);
        doNothing().when(semanticValidator).validate(dto);
        when(doctorDAO.findAllById(Set.of(99L))).thenReturn(List.of(seniorDoctor));

        AiScheduleConverterService service = new AiScheduleConverterService(jsonParser, semanticValidator, doctorDAO, shiftDAO);

        try {
            service.convert("payload");
            fail("Expected AiProtocolException");
        } catch (AiProtocolException ex) {
            assertEquals(AiProtocolException.ErrorCode.SCHEMA_MISMATCH, ex.getCode());
            assertTrue(ex.getMessage().contains("backend-provided role_validation_scratchpad contract"));
            assertTrue(ex.getDetails().stream().anyMatch(d -> "$.metadata.role_validation_scratchpad[0].candidate_doctor_ids[0]".equals(d.getPath())));
        }
    }


    private Doctor doctor(Long id, Seniority seniority) {
        Doctor doctor = mock(Doctor.class);
        when(doctor.getId()).thenReturn(id);
        when(doctor.getSeniority()).thenReturn(seniority);
        return doctor;
    }

    private AiAssignmentDto assignmentWithStatus(String shiftId,
                                                 Integer doctorId,
                                                 Seniority roleCovered,
                                                 ConcreteShiftDoctorStatus status) {
        AiAssignmentDto dto = assignment(shiftId, doctorId, roleCovered);
        dto.assignmentStatus = status;
        return dto;
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
        dto.assignmentStatus = ConcreteShiftDoctorStatus.ON_DUTY;
        return dto;
    }
}
