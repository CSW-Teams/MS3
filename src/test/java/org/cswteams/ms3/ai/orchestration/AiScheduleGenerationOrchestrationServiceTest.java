package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerRequest;
import org.cswteams.ms3.ai.broker.AiTokenBudgetGuardResult;
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
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        when(aiActiveConstraintResolver.resolve(any(), any())).thenReturn(List.of(
                new ToonActiveConstraint(ToonConstraintType.HARD, ToonConstraintEntityType.DOCTOR, String.valueOf(doctor.getId()), "REST_PERIOD", Map.of("hours", 11))
        ));
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));
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
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));
        verify(schedulerController, never()).persistSchedule(any(Schedule.class));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, atLeastOnce()).previewTokenBudget(requestCaptor.capture());

        String toonPayload = requestCaptor.getAllValues().get(0).getToonPayload();
        assertTrue(toonPayload.startsWith("ctx:{p:\""));
        assertTrue(toonPayload.contains("REST_PERIOD"));
        assertTrue(toonPayload.contains("hard_coverage_requirements[1]{shift_id,structured,specialist_junior,specialist_senior,total}:"));
        assertTrue(toonPayload.contains("S_1001_20260914,1,0,0,1"));
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

        String expectedBlock = "hard_coverage_requirements[2]{shift_id,structured,specialist_junior,specialist_senior,total}:\n"
                + "S_4001_20260914,0,0,0,0\n"
                + "S_4002_20260914,0,0,0,0\n";
        assertTrue(toonPayload.contains(expectedBlock));
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

        when(aiScheduleConverterService.convert(any()))
                .thenThrow(AiProtocolException.invalidFormat("first invalid candidate"))
                .thenReturn(List.of(concreteShift));

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                constraintDAO,
                holidayDAO,
                scheduleDAO,
                agentBroker,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, times(4)).requestSchedule(requestCaptor.capture());
        List<AiBrokerRequest> requests = requestCaptor.getAllValues();

        assertTrue(requests.get(0).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(1).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(1).getInstructions().contains("Previous attempt 1 was invalid"));
        assertTrue(requests.get(1).getInstructions().contains("Validation failures (prompt-safe):"));
        assertTrue(requests.get(2).getInstructions().contains("Use label EFFICIENT"));
        assertTrue(requests.get(3).getInstructions().contains("Use label BALANCED"));
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

    private String generateScheduleComparisonAndCaptureToonPayload(LocalDate startDate,
                                                                    LocalDate endDate,
                                                                    List<ConcreteShift> concreteShifts) {
        Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
        DoctorUffaPriority doctorPriority = new DoctorUffaPriority(doctor);
        doctorPriority.setGeneralPriority(3);
        doctorPriority.setNightPriority(4);
        doctorPriority.setLongShiftPriority(5);

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
        when(aiScheduleConverterService.convert(any())).thenReturn(concreteShifts);
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
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new AiHardCoveragePromptBlockBuilder(),
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));
        verify(schedulerController, never()).persistSchedule(any(Schedule.class));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, atLeastOnce()).previewTokenBudget(requestCaptor.capture());
        List<AiBrokerRequest> capturedRequests = requestCaptor.getAllValues();
        assertEquals(1, capturedRequests.size());
        return capturedRequests.get(0).getToonPayload();
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
}
