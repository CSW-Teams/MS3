package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerProperties;
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
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.DoctorHolidaysDAO;
import org.cswteams.ms3.dao.DoctorUffaPriorityDAO;
import org.cswteams.ms3.dao.HolidayDAO;
import org.cswteams.ms3.dao.RequestRemovalFromConcreteShiftDAO;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiScheduleGenerationOrchestrationServiceRetryValidationTest {

    @Test
    void validCandidatesAtFirstAttemptDoNotRetry() {
        RetryFixture fixture = new RetryFixture();
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("ok-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenReturn(List.of(fixture.convertedShift));

        fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(fixture.agentBroker, times(3)).requestSchedule(requestCaptor.capture());
        assertTrue(requestCaptor.getAllValues().stream().noneMatch(r -> r.getInstructions().contains("Previous attempt")));
    }

    @Test
    void invalidCandidateThenFixedWithinRetriesReturnsOnlyFixedFinalCandidate() {
        RetryFixture fixture = new RetryFixture();
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("bad-empathetic", "fixed-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("bad-empathetic")) {
                throw AiProtocolException.invalidFormat("bad candidate");
            }
            return List.of(fixture.convertedShift);
        });

        var response = fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        assertNotNull(response);
        var empathetic = response.getCandidates().stream()
                .filter(candidate -> "EMPATHETIC".equals(candidate.getMetadata().getType()))
                .findFirst()
                .orElseThrow();
        assertTrue(empathetic.getMetadata().isValid());
        assertFalse(empathetic.getMetadata().isMaxRetriesReached());
        assertTrue(empathetic.getRawScheduleText().contains("fixed-empathetic"));
        assertFalse(empathetic.getRawScheduleText().contains("bad-empathetic"));
    }

    @Test
    void retriesAreScopedPerVariantWhenOneVariantNeedsRetry() {
        RetryFixture fixture = new RetryFixture();
        fixture.aiBrokerProperties.setScheduleValidationMaxRetries(1);
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("bad-empathetic", "still-bad-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("empathetic")) {
                throw AiProtocolException.invalidFormat("empathetic invalid");
            }
            return List.of(fixture.convertedShift);
        });

        fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(fixture.agentBroker, times(4)).requestSchedule(requestCaptor.capture());
        List<AiBrokerRequest> requests = requestCaptor.getAllValues();
        assertTrue(requests.get(0).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(1).getInstructions().contains("Use label EMPATHETIC"));
        assertTrue(requests.get(2).getInstructions().contains("Use label EFFICIENT"));
        assertTrue(requests.get(3).getInstructions().contains("Use label BALANCED"));
    }

    @Test
    void maxRetriesExceededKeepsLastInvalidCandidate() {
        RetryFixture fixture = new RetryFixture();
        fixture.aiBrokerProperties.setScheduleValidationMaxRetries(1);
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("bad-empathetic", "last-bad-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("empathetic")) {
                throw AiProtocolException.invalidFormat("still invalid");
            }
            return List.of(fixture.convertedShift);
        });

        var response = fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        var empathetic = response.getCandidates().stream()
                .filter(candidate -> "EMPATHETIC".equals(candidate.getMetadata().getType()))
                .findFirst()
                .orElseThrow();
        assertFalse(empathetic.getMetadata().isValid());
        assertTrue(empathetic.getMetadata().isMaxRetriesReached());
        assertTrue(empathetic.getRawScheduleText().contains("last-bad-empathetic"));
    }

    @Test
    void violatedConstraintsAreEmbeddedInRetryPromptPayload() throws ViolatedConstraintException {
        RetryFixture fixture = new RetryFixture();
        fixture.aiBrokerProperties.setScheduleValidationMaxRetries(1);
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("violating-empathetic", "fixed-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));

        Constraint constraint = mock(Constraint.class);
        when(constraint.getId()).thenReturn(77L);
        doThrow(new ViolatedConstraintException("rest window violated")).when(constraint).verifyConstraint(any());
        when(fixture.constraintDAO.findAll()).thenReturn(List.of(constraint));

        Doctor assignedDoctor = fixture.newDoctor(42L, Seniority.STRUCTURED);
        ConcreteShift violatingShift = new ConcreteShift(fixture.startDate.toEpochDay(), fixture.shift);
        DoctorAssignment assignment = new DoctorAssignment(assignedDoctor,
                ConcreteShiftDoctorStatus.ON_DUTY,
                violatingShift,
                new Task(TaskEnum.CLINIC));
        violatingShift.setDoctorAssignmentList(List.of(assignment));

        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("violating-empathetic")) {
                return List.of(violatingShift);
            }
            return List.of(fixture.convertedShift);
        });

        fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(fixture.agentBroker, times(4)).requestSchedule(requestCaptor.capture());
        String retryInstructions = requestCaptor.getAllValues().get(1).getInstructions();

        assertTrue(retryInstructions.contains("Validation failures (prompt-safe):"));
        assertTrue(retryInstructions.contains("constraint_id='77'"));
        assertTrue(retryInstructions.contains("doctor_id='42'"));
        assertTrue(retryInstructions.contains("actual='rest window violated'"));
    }


    @Test
    void invalidCandidateIsRejectedOnSelectionPersist() {
        RetryFixture fixture = new RetryFixture();
        fixture.aiBrokerProperties.setScheduleValidationMaxRetries(0);
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("bad-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("bad-empathetic")) {
                throw AiProtocolException.invalidFormat("invalid selection candidate");
            }
            return List.of(fixture.convertedShift);
        });
        when(fixture.schedulerController.alreadyExistsAnotherSchedule(fixture.startDate, fixture.endDate)).thenReturn(false);

        fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);
        var result = fixture.service.persistSelectedCandidate("ai-empathetic");

        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.INVALID_SELECTION, result.getStatus());
        assertEquals("INVALID_CANDIDATE_SELECTION", result.getErrorCode());
        verify(fixture.schedulerController, never()).persistSchedule(any(Schedule.class));
    }

    @Test
    void retryCountUsesConfiguredPropertyValue() {
        RetryFixture fixture = new RetryFixture();
        fixture.aiBrokerProperties.setScheduleValidationMaxRetries(0);
        fixture.mockBrokerByReasoning(Map.of(
                "EMPATHETIC", List.of("bad-empathetic"),
                "EFFICIENT", List.of("ok-efficient"),
                "BALANCED", List.of("ok-balanced")
        ));
        when(fixture.aiScheduleConverterService.convert(any())).thenAnswer(invocation -> {
            String rawJson = invocation.getArgument(0);
            if (rawJson.contains("bad-empathetic")) {
                throw AiProtocolException.invalidFormat("invalid once");
            }
            return List.of(fixture.convertedShift);
        });

        var response = fixture.service.generateScheduleComparison(fixture.startDate, fixture.endDate);

        verify(fixture.agentBroker, times(3)).requestSchedule(any());
        assertFalse(response.getCandidates().stream()
                .filter(candidate -> "EMPATHETIC".equals(candidate.getMetadata().getType()))
                .findFirst()
                .orElseThrow()
                .getMetadata()
                .isValid());
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

    private class RetryFixture {
        private final LocalDate startDate = LocalDate.of(2026, 9, 14);
        private final LocalDate endDate = LocalDate.of(2026, 9, 14);
        private final Shift shift = makeShift(1001L);
        private final ConcreteShift convertedShift = new ConcreteShift(startDate.toEpochDay(), shift);
        private final Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(convertedShift));

        private final ISchedulerController schedulerController = mock(ISchedulerController.class);
        private final ConstraintDAO constraintDAO = mock(ConstraintDAO.class);
        private final DoctorDAO doctorDAO = mock(DoctorDAO.class);
        private final DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        private final DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        private final HolidayDAO holidayDAO = mock(HolidayDAO.class);
        private final ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        private final AgentBroker agentBroker = mock(AgentBroker.class);
        private final AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        private final DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        private final AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        private final RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);
        private final AiBrokerProperties aiBrokerProperties = new AiBrokerProperties();
        private final Map<String, Integer> attemptsByLabel = new HashMap<>();

        private final AiScheduleGenerationOrchestrationService service;

        private RetryFixture() {
            AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                    new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

            Doctor doctor = newDoctor(10L, Seniority.STRUCTURED);
            DoctorUffaPriority priority = new DoctorUffaPriority(doctor);
            priority.setGeneralPriority(1);
            priority.setNightPriority(1);
            priority.setLongShiftPriority(1);

            when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
            when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                    .thenReturn(List.of());
            when(constraintDAO.findAll()).thenReturn(List.of());
            when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
            when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(priority));
            when(doctorUffaPriorityDAO.findAll()).thenReturn(List.of(priority));
            when(doctorHolidaysDAO.findAll()).thenReturn(List.of());
            when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
            when(holidayDAO.findAll()).thenReturn(List.of());
            when(agentBroker.previewTokenBudget(any())).thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
            when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                    .thenReturn(new AuditedSelectionResult("standard", List.of()));

            service = new AiScheduleGenerationOrchestrationService(
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
                    new ObjectMapper()
            );
        }

        private void mockBrokerByReasoning(Map<String, List<String>> responsesByLabel) {
            when(agentBroker.requestSchedule(any())).thenAnswer(invocation -> {
                AiBrokerRequest request = invocation.getArgument(0);
                String label = extractLabel(request.getInstructions());
                List<String> responses = responsesByLabel.get(label);
                if (responses == null || responses.isEmpty()) {
                    throw new IllegalStateException("No response configured for label " + label);
                }
                int attemptIndex = attemptsByLabel.getOrDefault(label, 0);
                attemptsByLabel.put(label, attemptIndex + 1);
                int responseIndex = Math.min(attemptIndex, responses.size() - 1);
                String reasoning = responses.get(responseIndex);
                return new AiScheduleVariantsResponse(Map.of(label, aiVariantResponse(reasoning)));
            });
        }

        private String extractLabel(String instructions) {
            if (instructions.contains("Use label EMPATHETIC")) {
                return "EMPATHETIC";
            }
            if (instructions.contains("Use label EFFICIENT")) {
                return "EFFICIENT";
            }
            return "BALANCED";
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

        private Shift makeShift(Long id) {
            Task task = new Task(TaskEnum.CLINIC);
            MedicalService service = new MedicalService(List.of(task), "Ward");
            return new Shift(
                    id,
                    TimeSlot.MORNING,
                    LocalTime.of(8, 0),
                    Duration.ofHours(6),
                    Set.of(DayOfWeek.MONDAY),
                    service,
                    List.of(new QuantityShiftSeniority(Map.of(Seniority.STRUCTURED, 1), task)),
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
}
