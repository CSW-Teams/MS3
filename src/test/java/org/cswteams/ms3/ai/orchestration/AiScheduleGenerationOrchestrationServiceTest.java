package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerProperties;
import org.cswteams.ms3.ai.broker.AiBrokerRequest;
import org.cswteams.ms3.ai.broker.AiTokenBudgetGuardResult;
import org.cswteams.ms3.ai.broker.domain.AiAssignment;
import org.cswteams.ms3.ai.broker.domain.AiMetadata;
import org.cswteams.ms3.ai.broker.domain.AiMetrics;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiScheduleVariantsResponse;
import org.cswteams.ms3.ai.decision.DecisionAlgorithmService;
import org.cswteams.ms3.ai.protocol.converter.AiScheduleConverterService;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.audit.selection.AuditedSelectionResult;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonConstraintEntityType;
import org.cswteams.ms3.control.toon.ToonConstraintType;
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.DoctorHolidaysDAO;
import org.cswteams.ms3.dao.DoctorUffaPriorityDAO;
import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dao.RequestRemovalFromConcreteShiftDAO;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.constraint.ConstraintUbiquita;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiScheduleGenerationOrchestrationServiceTest {

    @Test
    void generateScheduleComparisonBuildsCompactToonPayloadWithoutValidationException() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 14);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        Shift shift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift concreteShift = new ConcreteShift(startDate.toEpochDay(), shift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());

        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
        ToonActiveConstraint restPeriodConstraint = new ToonActiveConstraint(
                ToonConstraintType.HARD,
                ToonConstraintEntityType.DOCTOR,
                String.valueOf(doctor.getId()),
                "REST_PERIOD",
                Map.of("hours", "11")
        );
        when(aiActiveConstraintResolver.resolveWithReport(any(), any(), anyBoolean())).thenReturn(
                new AiActiveConstraintResolver.ResolveResult(List.of(restPeriodConstraint), 0, 1, 0)
        );
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        AiBrokerProperties aiBrokerProperties = new AiBrokerProperties();

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                aiBrokerProperties,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));
        verify(schedulerController, never()).persistSchedule(any(Schedule.class));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, atLeastOnce()).previewTokenBudget(requestCaptor.capture());

        boolean found = requestCaptor.getAllValues().stream()
                .map(AiBrokerRequest::getToonPayload)
                .filter(payload -> payload != null)
                .anyMatch(payload -> payload.startsWith("ctx:{p:\"")
                        && payload.contains("REST_PERIOD")
                        && payload.contains("hard_coverage_requirements[1]{shift_id,structured,specialist_junior,specialist_senior,total}:")
                        && payload.contains("S_1001_20260914,1,0,0,1"));

        assertTrue(found);
    }

    @Test
    void generateScheduleComparisonAppendsRoleValidationScratchpadWithBackendFilteredDoctorIds() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(1101L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));

        String toonPayload = generateScheduleComparisonAndCaptureToonPayloadWithDoctors(date, date, List.of(
                new ConcreteShift(date.toEpochDay(), shift)
        ), List.of(
                newDoctor(10L, Seniority.STRUCTURED),
                newDoctor(20L, Seniority.SPECIALIST_JUNIOR),
                newDoctor(30L, Seniority.SPECIALIST_SENIOR)
        ));

        int hardCoverageIndex = toonPayload.indexOf("hard_coverage_requirements[");
        int roleValidationIndex = toonPayload.indexOf("role_validation_scratchpad[");
        assertTrue(hardCoverageIndex > 0);
        assertTrue(roleValidationIndex > hardCoverageIndex);
        assertTrue(toonPayload.contains("role_validation_scratchpad[2]{shift_id,required_role,required_count,candidate_doctor_ids}:"));
        assertTrue(toonPayload.contains("S_1101_20260914,STRUCTURED,1,[10]"));
        assertTrue(toonPayload.contains("S_1101_20260914,SPECIALIST_JUNIOR,1,[20]"));
        assertFalse(toonPayload.contains("S_1101_20260914,SPECIALIST_SENIOR"));
    }

    @Test
    void generateScheduleComparisonSerializesSingleShiftWithMultipleSeniorityMinima() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(2001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1)),
                quantity(Map.of(Seniority.SPECIALIST_SENIOR, 2))
        ));

        String toonPayload = generateScheduleComparisonAndCaptureToonPayload(date, date, List.of(
                new ConcreteShift(date.toEpochDay(), shift)
        ));

        assertTrue(toonPayload.contains("hard_coverage_requirements[1]{shift_id,structured,specialist_junior,specialist_senior,total}:"));
        assertTrue(toonPayload.contains("S_2001_20260914,1,0,2,3"));
        assertEquals(1, countOccurrences(toonPayload, "hard_coverage_requirements["));
        assertEquals(0, countOccurrences(toonPayload, "on_call_coverage_requirements["));
    }

    @Test
    void generateScheduleComparisonSerializesOneHardCoverageRowPerShiftInDeterministicOrder() {
        LocalDate firstDay = LocalDate.of(2026, 9, 14);
        LocalDate secondDay = LocalDate.of(2026, 9, 15);

        Shift firstShift = makeShift(3002L, TimeSlot.AFTERNOON, LocalTime.of(13, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.SPECIALIST_JUNIOR, 1, Seniority.SPECIALIST_SENIOR, 1))
        ));
        Shift secondShift = makeShift(3001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 2))
        ));

        String toonPayload = generateScheduleComparisonAndCaptureToonPayload(firstDay, secondDay, List.of(
                new ConcreteShift(secondDay.toEpochDay(), firstShift),
                new ConcreteShift(firstDay.toEpochDay(), secondShift)
        ));

        String expectedBlock = "hard_coverage_requirements[2]{shift_id,structured,specialist_junior,specialist_senior,total}:\n"
                + "S_3001_20260914,2,0,0,2\n"
                + "S_3002_20260915,0,1,1,2\n";
        assertTrue(toonPayload.contains(expectedBlock));
        assertTrue(toonPayload.contains("S_3001_20260914"));
        assertTrue(toonPayload.contains("S_3002_20260915"));
    }

    @Test
    void generateScheduleComparisonSerializesAbsentOrZeroSeniorityMinimaAsZeroes() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift absentMinimaShift = makeShift(4001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of());
        Shift zeroMinimaShift = makeShift(4002L, TimeSlot.AFTERNOON, LocalTime.of(13, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(
                        Seniority.STRUCTURED, 0,
                        Seniority.SPECIALIST_JUNIOR, 0,
                        Seniority.SPECIALIST_SENIOR, -3
                ))
        ));

        String toonPayload = generateScheduleComparisonAndCaptureToonPayload(date, date, List.of(
                new ConcreteShift(date.toEpochDay(), zeroMinimaShift),
                new ConcreteShift(date.toEpochDay(), absentMinimaShift)
        ));

        assertTrue(toonPayload.contains("hard_coverage_requirements[2]{shift_id,structured,specialist_junior,specialist_senior,total}:"));
        assertTrue(toonPayload.contains("S_4001_20260914,0,0,0,0"));
        assertTrue(toonPayload.contains("S_4002_20260914,0,0,0,0"));
    }


    @Test
    void generateScheduleComparisonRetriesInvalidVariantIndependently() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 14);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        Shift shift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift concreteShift = new ConcreteShift(startDate.toEpochDay(), shift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(true, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        when(agentBroker.requestSchedule(any())).thenAnswer(invocation -> {
            AiBrokerRequest request = invocation.getArgument(0);
            if (request.getInstructions().contains("Use label EMPATHETIC")) {
                return new AiScheduleVariantsResponse(Map.of("EMPATHETIC", aiVariantResponse("retry-empathetic")));
            }
            if (request.getInstructions().contains("Use label EFFICIENT")) {
                return new AiScheduleVariantsResponse(Map.of("EFFICIENT", aiVariantResponse("efficient")));
            }
            return new AiScheduleVariantsResponse(Map.of("BALANCED", aiVariantResponse("balanced")));
        });

        when(aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson != null && rawJson.contains("retry-empathetic")) {
                throw AiProtocolException.invalidFormat("empathetic candidate remains invalid");
            }
            return List.of(concreteShift);
        });

        AiBrokerProperties aiBrokerProperties = new AiBrokerProperties();
        aiBrokerProperties.setScheduleValidationMaxRetries(1);

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                aiBrokerProperties,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        var response = assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, times(4)).requestSchedule(requestCaptor.capture());
        List<AiBrokerRequest> requests = requestCaptor.getAllValues();

        assertTrue(requests.get(0).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(1).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(1).getInstructions().contains("Previous attempt 1 was invalid"));
        assertTrue(requests.get(1).getInstructions().contains("Validation failures (prompt-safe):"));
        assertTrue(requests.get(1).getInstructions().contains("backend-authoritative contract"));
        assertTrue(requests.get(2).getInstructions().contains("Use label EFFICIENT"));
        assertTrue(requests.get(3).getInstructions().contains("Use label BALANCED"));

        var empatheticCandidate = response.getCandidates().stream()
                .filter(candidate -> "EMPATHETIC".equalsIgnoreCase(candidate.getMetadata().getType()))
                .findFirst()
                .orElseThrow();

        assertFalse(empatheticCandidate.getMetadata().isValid());
        assertTrue(empatheticCandidate.getMetadata().isMaxRetriesReached());
        assertTrue(empatheticCandidate.getMetadata().getValidationCode().contains("CONVERSION_FAILED"));
        assertTrue(empatheticCandidate.getMetadata().getValidationViolations().isEmpty());
        assertTrue(empatheticCandidate.getRawScheduleText().contains("retry-empathetic"));
    }

    @Test
    void generateScheduleComparisonWhenAiReturnsWrongRoleLogsDiagnosticsAndPublishesValidationMetadata() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 14);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        Shift shift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift concreteShift = new ConcreteShift(startDate.toEpochDay(), shift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any())).thenReturn(new AiTokenBudgetGuardResult(true, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));
        when(agentBroker.requestSchedule(any())).thenAnswer(invocation -> {
            AiBrokerRequest request = invocation.getArgument(0);
            if (request.getInstructions().contains("Use label EMPATHETIC")) {
                return new AiScheduleVariantsResponse(Map.of("EMPATHETIC", aiVariantResponse("wrong-role")));
            }
            if (request.getInstructions().contains("Use label EFFICIENT")) {
                return new AiScheduleVariantsResponse(Map.of("EFFICIENT", aiVariantResponse("efficient")));
            }
            return new AiScheduleVariantsResponse(Map.of("BALANCED", aiVariantResponse("balanced")));
        });

        List<org.cswteams.ms3.ai.protocol.ValidationError> roleMismatchDetails = List.of(
                new org.cswteams.ms3.ai.protocol.ValidationError(
                        "$.metadata.role_validation_scratchpad[0].candidate_doctor_ids[0]",
                        "doctor_id=10 has seniority=STRUCTURED but role_required=SPECIALIST_JUNIOR (backend-authoritative scratchpad semantics violated)"
                )
        );
        when(aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson != null && rawJson.contains("wrong-role")) {
                throw AiProtocolException.schemaMismatch(
                        "AI response violates the backend-provided role_validation_scratchpad contract: candidate_doctor_ids do not match role_required",
                        roleMismatchDetails,
                        null
                );
            }
            return List.of(concreteShift);
        });

        AiBrokerProperties aiBrokerProperties = new AiBrokerProperties();
        aiBrokerProperties.setScheduleValidationMaxRetries(0);

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                aiBrokerProperties,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        Logger serviceLogger = (Logger) org.slf4j.LoggerFactory.getLogger(AiScheduleGenerationOrchestrationService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        serviceLogger.addAppender(appender);

        try {
            var response = assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

            var empatheticCandidate = response.getCandidates().stream()
                    .filter(candidate -> "EMPATHETIC".equalsIgnoreCase(candidate.getMetadata().getType()))
                    .findFirst()
                    .orElseThrow();

            assertFalse(empatheticCandidate.getMetadata().isValid());
            assertEquals("CONVERSION_FAILED", empatheticCandidate.getMetadata().getValidationCode());
            assertTrue(empatheticCandidate.getMetadata().getValidationViolations().stream()
                    .anyMatch(message -> message.contains("role_validation_scratchpad")
                            && message.contains("backend-authoritative scratchpad semantics")
                            && message.contains("role_required=SPECIALIST_JUNIOR")));

            ILoggingEvent mismatchLog = appender.list.stream()
                    .filter(event -> event.getFormattedMessage().contains("event=ai_candidate_role_mismatch_validation_failed"))
                    .findFirst()
                    .orElse(null);
            assertNotNull(mismatchLog);
            assertEquals(Level.WARN, mismatchLog.getLevel());
            String logMessage = mismatchLog.getFormattedMessage();
            assertTrue(logMessage.contains("variant_id=EMPATHETIC"));
            assertTrue(logMessage.contains("candidate_id=ai-empathetic"));
            assertTrue(logMessage.contains("role_mismatch_count=1"));
            assertTrue(logMessage.contains("expected_seniority='SPECIALIST_JUNIOR'"));
            assertTrue(logMessage.contains("actual='STRUCTURED'"));
        } finally {
            serviceLogger.detachAppender(appender);
        }
    }


    @Test
    void generateScheduleComparisonKeepsAssignmentStatusInAiRoundTripPayload() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 14);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        Shift shift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift concreteShift = new ConcreteShift(startDate.toEpochDay(), shift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(true, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));
        when(agentBroker.requestSchedule(any())).thenReturn(new AiScheduleVariantsResponse(Map.of(
                "EMPATHETIC", new AiScheduleResponse(
                        AiStatus.SUCCESS,
                        new AiMetadata("empathetic", 0.9, new AiMetrics(100.0, null, 0)),
                        List.of(new AiAssignment(
                                "S_1001_20260914",
                                10,
                                Seniority.STRUCTURED,
                                ConcreteShiftDoctorStatus.ON_DUTY,
                                false,
                                null
                        )),
                        List.of(),
                        List.of()
                ),
                "EFFICIENT", aiVariantResponse("efficient"),
                "BALANCED", aiVariantResponse("balanced")
        )));
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                new AiBrokerProperties(),
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(aiScheduleConverterService, atLeastOnce()).convert(payloadCaptor.capture());

        assertTrue(payloadCaptor.getAllValues().stream()
                .anyMatch(payload -> payload.contains("\"assignment_status\":\"ON_DUTY\"")));
    }


    @Test
    void computeCoverageTreatsMissingOnCallLayerAsIncompleteCoverage() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(9101L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(1L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(2L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));

        double coverage = invokeComputeCoverage(coverageService(), List.of(concreteShift));

        assertEquals(0.5, coverage, 0.0001);
    }

    @Test
    void computeCoverageReturns100PercentWhenBothLayersMeetMinima() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(9102L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(1L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(2L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(3L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(4L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC)));

        double coverage = invokeComputeCoverage(coverageService(), List.of(concreteShift));

        assertEquals(1.0, coverage, 0.0001);
    }

    @Test
    void computeCoverageDegradesProportionallyWhenOneLayerIsPartiallyCovered() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(9103L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(1L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(2L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)));
        concreteShift.getDoctorAssignmentList().add(new DoctorAssignment(newDoctor(3L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC)));

        double coverage = invokeComputeCoverage(coverageService(), List.of(concreteShift));

        assertEquals(0.75, coverage, 0.0001);
    }

    @Test
    void generateScheduleComparisonRejectsHalfOutputsWithOnlyOnDutyLayer() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(9201L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        concreteShift.setDoctorAssignmentList(List.of(
                new DoctorAssignment(newDoctor(1L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(2L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(3L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(4L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC))
        ));

        Schedule transientSchedule = new Schedule(date.toEpochDay(), date.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        when(schedulerController.createScheduleTransient(date, date)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(date.toEpochDay(), date.toEpochDay())).thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any())).thenReturn(new AiTokenBudgetGuardResult(true, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));
        when(agentBroker.requestSchedule(any())).thenReturn(new AiScheduleVariantsResponse(Map.of(
                "EMPATHETIC", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9201_20260914", 1, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9201_20260914", 2, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null)
                )),
                "EFFICIENT", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9201_20260914", 11, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9201_20260914", 12, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9201_20260914", 13, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL, false, null),
                        new AiAssignment("S_9201_20260914", 14, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL, false, null)
                )),
                "BALANCED", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9201_20260914", 21, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9201_20260914", 22, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9201_20260914", 23, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL, false, null),
                        new AiAssignment("S_9201_20260914", 24, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL, false, null)
                ))
        )));
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                new AiBrokerProperties(),
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        var result = service.generateScheduleComparison(date, date);

        assertNotNull(result);
        assertTrue(result.getCandidates().stream()
                .anyMatch(candidate -> "ai-empathetic".equals(candidate.getMetadata().getCandidateId())
                        && !candidate.isValid()
                        && candidate.getValidationErrors().stream().anyMatch(error -> error.contains("assignment_status=ON_CALL"))));

        ArgumentCaptor<List> metricsCaptor = ArgumentCaptor.forClass(List.class);
        verify(decisionAlgorithmService).selectPreferredWithAudit(metricsCaptor.capture());
        List<?> passedToRanker = metricsCaptor.getValue();
        assertEquals(3, passedToRanker.size());
    }

    @Test
    void generateScheduleComparisonKeepsFullyLayeredOutputsRankable() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Shift shift = makeShift(9202L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360), List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1))
        ));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        concreteShift.setDoctorAssignmentList(List.of(
                new DoctorAssignment(newDoctor(1L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(2L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(3L, Seniority.STRUCTURED), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC)),
                new DoctorAssignment(newDoctor(4L, Seniority.SPECIALIST_JUNIOR), ConcreteShiftDoctorStatus.ON_CALL, concreteShift, new Task(TaskEnum.CLINIC))
        ));

        Schedule transientSchedule = new Schedule(date.toEpochDay(), date.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        when(schedulerController.createScheduleTransient(date, date)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(date.toEpochDay(), date.toEpochDay())).thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any())).thenReturn(new AiTokenBudgetGuardResult(true, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));
        when(agentBroker.requestSchedule(any())).thenReturn(new AiScheduleVariantsResponse(Map.of(
                "EMPATHETIC", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9202_20260914", 1, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 2, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 3, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL, false, null),
                        new AiAssignment("S_9202_20260914", 4, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL, false, null)
                )),
                "EFFICIENT", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9202_20260914", 11, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 12, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 13, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL, false, null),
                        new AiAssignment("S_9202_20260914", 14, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL, false, null)
                )),
                "BALANCED", aiVariantResponseWithAssignments(List.of(
                        new AiAssignment("S_9202_20260914", 21, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 22, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_DUTY, false, null),
                        new AiAssignment("S_9202_20260914", 23, Seniority.STRUCTURED, ConcreteShiftDoctorStatus.ON_CALL, false, null),
                        new AiAssignment("S_9202_20260914", 24, Seniority.SPECIALIST_JUNIOR, ConcreteShiftDoctorStatus.ON_CALL, false, null)
                ))
        )));
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("ai-balanced", List.of()));

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                new AiBrokerProperties(),
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        var result = service.generateScheduleComparison(date, date);

        assertNotNull(result);
        assertTrue(result.getCandidates().stream().filter(c -> !"standard".equals(c.getMetadata().getCandidateId())).allMatch(c -> c.isValid()));
        ArgumentCaptor<List> metricsCaptor = ArgumentCaptor.forClass(List.class);
        verify(decisionAlgorithmService).selectPreferredWithAudit(metricsCaptor.capture());
        assertEquals(4, metricsCaptor.getValue().size());
    }


    private AiScheduleResponse aiVariantResponse(String reasoning) {
        return new AiScheduleResponse(
                AiStatus.SUCCESS,
                new AiMetadata(reasoning, 0.9, new AiMetrics(100.0, null, 0)),
                List.of(),
                List.of(),
                List.of()
        );
    }

    private AiScheduleResponse aiVariantResponseWithAssignments(List<AiAssignment> assignments) {
        return new AiScheduleResponse(
                AiStatus.SUCCESS,
                new AiMetadata("layered", 0.9, new AiMetrics(100.0, null, 0)),
                assignments,
                List.of(),
                List.of()
        );
    }


    @Test
    void generateScheduleComparisonMarksCandidateInvalidWhenConvertedShiftIsStructurallyMalformed() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 14);

        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        Shift validShift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift transientConcreteShift = new ConcreteShift(startDate.toEpochDay(), validShift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(transientConcreteShift));

        ConcreteShift missingShiftReference = new ConcreteShift(startDate.toEpochDay(), null);
        missingShiftReference.setDoctorAssignmentList(List.of());

        Shift shiftWithMissingDuration = makeShift(1002L, TimeSlot.AFTERNOON, LocalTime.of(14, 0), Duration.ofMinutes(240));
        shiftWithMissingDuration.setDuration(null);
        ConcreteShift missingDurationShift = new ConcreteShift(startDate.toEpochDay(), shiftWithMissingDuration);
        missingDurationShift.setDoctorAssignmentList(List.of());

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        when(agentBroker.requestSchedule(any())).thenAnswer(invocation -> {
            AiBrokerRequest request = invocation.getArgument(0);
            if (request.getInstructions().contains("Use label EMPATHETIC")) {
                return new AiScheduleVariantsResponse(Map.of("EMPATHETIC", aiVariantResponse("malformed-empathetic")));
            }
            if (request.getInstructions().contains("Use label EFFICIENT")) {
                return new AiScheduleVariantsResponse(Map.of("EFFICIENT", aiVariantResponse("efficient")));
            }
            return new AiScheduleVariantsResponse(Map.of("BALANCED", aiVariantResponse("balanced")));
        });

        when(aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson != null && rawJson.contains("malformed-empathetic")) {
                return List.of(missingShiftReference, missingDurationShift);
            }
            return List.of(transientConcreteShift);
        });

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                new AiBrokerProperties(),
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        var response = assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

        var empatheticCandidate = response.getCandidates().stream()
                .filter(candidate -> "EMPATHETIC".equalsIgnoreCase(candidate.getMetadata().getType()))
                .findFirst()
                .orElseThrow();

        assertFalse(empatheticCandidate.getMetadata().isValid());
        assertEquals("DOMAIN_CONSTRAINTS_VIOLATED", empatheticCandidate.getMetadata().getValidationCode());
        assertTrue(empatheticCandidate.getMetadata().getValidationViolations().stream()
                .anyMatch(message -> message.contains("Missing required field: shift")));
        assertTrue(empatheticCandidate.getMetadata().getValidationViolations().stream()
                .anyMatch(message -> message.contains("Missing required field: shift.duration")));
    }

    @Test
    void collectViolatedConstraintsDoesNotReportSelfOverlapForSingleAssignment() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 14);
        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        Shift shift = makeShift(1001L, TimeSlot.MORNING, LocalTime.of(8, 0), Duration.ofMinutes(360));
        ConcreteShift concreteShift = new ConcreteShift(date.toEpochDay(), shift);
        DoctorAssignment assignment = new DoctorAssignment(doctor, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift, new Task(TaskEnum.CLINIC));
        concreteShift.getDoctorAssignmentList().add(assignment);
        Schedule candidateSchedule = new Schedule(date.toEpochDay(), date.toEpochDay(), List.of(concreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(constraintDAO.findAll()).thenReturn(List.of(new ConstraintUbiquita()));
        when(doctorUffaPriorityDAO.findAll()).thenReturn(List.of(new DoctorUffaPriority(doctor)));
        when(doctorHolidaysDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                new AiBrokerProperties(),
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        Method collectViolatedConstraints = AiScheduleGenerationOrchestrationService.class
                .getDeclaredMethod("collectViolatedConstraints", Schedule.class);
        collectViolatedConstraints.setAccessible(true);

        List<?> violations = (List<?>) collectViolatedConstraints.invoke(service, candidateSchedule);

        assertTrue(violations.isEmpty());
    }

    private String generateScheduleComparisonAndCaptureToonPayload(LocalDate startDate,
                                                                    LocalDate endDate,
                                                                    List<ConcreteShift> concreteShifts) {
        return generateScheduleComparisonAndCaptureToonPayloadWithDoctors(startDate,
                endDate,
                concreteShifts,
                List.of(newDoctor(10L, Seniority.STRUCTURED)));
    }

    private String generateScheduleComparisonAndCaptureToonPayloadWithDoctors(LocalDate startDate,
                                                                               LocalDate endDate,
                                                                               List<ConcreteShift> concreteShifts,
                                                                               List<Doctor> doctors) {
        List<Doctor> effectiveDoctors = doctors == null || doctors.isEmpty()
                ? List.of(newDoctor(10L, Seniority.STRUCTURED))
                : doctors;
        Doctor primaryDoctor = effectiveDoctors.get(0);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(primaryDoctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

        for (ConcreteShift concreteShift : concreteShifts) {
            if (concreteShift == null) {
                continue;
            }
            if (concreteShift.getDoctorAssignmentList() != null
                    && !concreteShift.getDoctorAssignmentList().isEmpty()) {
                continue;
            }
            DoctorAssignment assignment = new DoctorAssignment(
                    primaryDoctor,
                    ConcreteShiftDoctorStatus.ON_DUTY,
                    concreteShift,
                    new Task(TaskEnum.CLINIC)
            );
            concreteShift.setDoctorAssignmentList(List.of(assignment));
        }

        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), concreteShifts);

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        HolidayDAO holidayDAO = mock(HolidayDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(effectiveDoctors);
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(primaryDoctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(primaryDoctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(constraintDAO.findAll()).thenReturn(List.of());
        when(holidayDAO.findAll()).thenReturn(List.of());
        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of());
        when(aiScheduleConverterService.convert(any())).thenReturn(concreteShifts);
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        AiBrokerProperties aiBrokerProperties = new AiBrokerProperties();

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                aiBrokerProperties,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));
        verify(schedulerController, never()).persistSchedule(any(Schedule.class));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, atLeastOnce()).previewTokenBudget(requestCaptor.capture());
        List<AiBrokerRequest> capturedRequests = requestCaptor.getAllValues();
        assertFalse(capturedRequests.isEmpty());

        String firstNonBlankPayload = null;
        for (AiBrokerRequest capturedRequest : capturedRequests) {
            String toonPayload = capturedRequest.getToonPayload();
            if (toonPayload == null || toonPayload.isBlank()) {
                continue;
            }
            if (firstNonBlankPayload == null) {
                firstNonBlankPayload = toonPayload;
            }
            if (toonPayload.contains("hard_coverage_requirements[")) {
                return toonPayload;
            }
        }

        if (firstNonBlankPayload != null) {
            return firstNonBlankPayload;
        }
        fail("No captured toon payload containing hard_coverage_requirements");
        return null;
    }


    private AiScheduleGenerationOrchestrationService coverageService() {
        return new AiScheduleGenerationOrchestrationService(
                mock(ISchedulerController.class),
                mock(DoctorDAO.class),
                mock(DoctorUffaPriorityDAO.class),
                mock(DoctorHolidaysDAO.class),
                mock(ConstraintDAO.class),
                mock(HolidayDAO.class),
                mock(ScheduleDAO.class),
                mock(AgentBroker.class),
                new AiBrokerProperties(),
                new AiReschedulingOrchestrationService(mock(RequestRemovalFromConcreteShiftDAO.class), mock(AiActiveConstraintResolver.class)),
                mock(DecisionAlgorithmService.class),
                mock(AiScheduleConverterService.class),
                new AiHardCoveragePromptBlockBuilder(),
                new AiRoleValidationScratchpadPromptBlockBuilder(),
                new ObjectMapper()
        );
    }

    private double invokeComputeCoverage(AiScheduleGenerationOrchestrationService service,
                                         List<ConcreteShift> concreteShifts) throws Exception {
        Method computeCoverage = AiScheduleGenerationOrchestrationService.class
                .getDeclaredMethod("computeCoverage", List.class);
        computeCoverage.setAccessible(true);
        return (double) computeCoverage.invoke(service, concreteShifts);
    }

    private Doctor newDoctor(Long id, Seniority seniority) {
        Doctor doctor = new Doctor(
                "Mario",
                "Rossi",
                "TAXCODEX00000000",
                LocalDate.of(1980, 1, 1),
                "doctor@example.com",
                "secret",
                seniority,
                Set.of(SystemActor.DOCTOR)
        );
        setField(doctor, "id", id);
        return doctor;
    }

    private QuantityShiftSeniority quantity(Map<Seniority, Integer> seniorityMap) {
        return new QuantityShiftSeniority(seniorityMap, new Task(TaskEnum.CLINIC));
    }

    private Shift makeShift(Long id, TimeSlot timeSlot, LocalTime startTime, Duration duration) {
        return makeShift(id, timeSlot, startTime, duration, List.of(
                quantity(Map.of(Seniority.STRUCTURED, 1))
        ));
    }

    private Shift makeShift(Long id,
                            TimeSlot timeSlot,
                            LocalTime startTime,
                            Duration duration,
                            List<QuantityShiftSeniority> quantities) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        return new Shift(
                id,
                timeSlot,
                startTime,
                duration,
                Set.of(DayOfWeek.MONDAY),
                service,
                quantities,
                List.of()
        );
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to set field " + fieldName, ex);
        }
    }

    private int countOccurrences(String source, String token) {
        int count = 0;
        int fromIndex = 0;
        while ((fromIndex = source.indexOf(token, fromIndex)) >= 0) {
            count++;
            fromIndex += token.length();
        }
        return count;
    }
}
