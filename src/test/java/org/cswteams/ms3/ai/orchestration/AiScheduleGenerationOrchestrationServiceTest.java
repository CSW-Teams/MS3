package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
import org.cswteams.ms3.ai.broker.AiBrokerRequest;
import org.cswteams.ms3.ai.broker.AiTokenBudgetGuardResult;
import org.cswteams.ms3.ai.decision.DecisionAlgorithmService;
import org.cswteams.ms3.ai.protocol.converter.AiScheduleConverterService;
import org.cswteams.ms3.audit.selection.AuditedSelectionResult;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.DoctorHolidaysDAO;
import org.cswteams.ms3.dao.DoctorUffaPriorityDAO;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
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
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(scheduleDAO.save(transientSchedule)).thenReturn(transientSchedule);
        when(doctorDAO.findBySeniorities(any())).thenReturn(List.of(doctor));
        when(doctorUffaPriorityDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of(doctorPriority));
        when(doctorHolidaysDAO.findByDoctor_IdIn(List.of(doctor.getId()))).thenReturn(List.of());
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());

        when(agentBroker.previewTokenBudget(any()))
                .thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(concreteShift));
        when(decisionAlgorithmService.selectPreferredWithAudit(any()))
                .thenReturn(new AuditedSelectionResult("standard", List.of()));

        AiScheduleGenerationOrchestrationService service = new AiScheduleGenerationOrchestrationService(
                schedulerController,
                doctorDAO,
                doctorUffaPriorityDAO,
                doctorHolidaysDAO,
                scheduleDAO,
                agentBroker,
                aiReschedulingOrchestrationService,
                decisionAlgorithmService,
                aiScheduleConverterService,
                new ObjectMapper()
        );

        assertDoesNotThrow(() -> service.generateScheduleComparison(startDate, endDate));

        ArgumentCaptor<AiBrokerRequest> requestCaptor = ArgumentCaptor.forClass(AiBrokerRequest.class);
        verify(agentBroker, atLeastOnce()).previewTokenBudget(requestCaptor.capture());

        String toonPayload = requestCaptor.getAllValues().get(0).getToonPayload();
        assertTrue(toonPayload.startsWith("ctx:{p:\""));
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

    private Shift makeShift(Long id, TimeSlot timeSlot, LocalTime startTime, Duration duration) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(Map.of(
                Seniority.STRUCTURED, 1
        ), task);
        return new Shift(
                id,
                timeSlot,
                startTime,
                duration,
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
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
