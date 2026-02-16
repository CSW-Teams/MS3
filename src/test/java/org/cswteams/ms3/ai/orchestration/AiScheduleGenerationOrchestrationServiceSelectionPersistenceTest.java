package org.cswteams.ms3.ai.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.ai.broker.AgentBroker;
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
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Shift;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiScheduleGenerationOrchestrationServiceSelectionPersistenceTest {

    @Test
    void persistCandidateBlocksExactDuplicateRangeAndKeepsStateUntilSuccess() {
        TestContext ctx = buildContext();
        when(ctx.schedulerController.alreadyExistsAnotherSchedule(ctx.startDate, ctx.endDate)).thenReturn(true);

        ctx.service.generateScheduleComparison(ctx.startDate, ctx.endDate);

        AiScheduleGenerationOrchestrationService.SelectionResult duplicateResult =
                ctx.service.persistSelectedCandidate("standard");

        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.DUPLICATE_RANGE,
                duplicateResult.getStatus());
        assertEquals("DUPLICATE_RANGE", duplicateResult.getErrorCode());
        verify(ctx.schedulerController, never()).persistSchedule(any(Schedule.class));
        assertNotNull(ctx.service.getLatestComparison());

        Schedule persistedSchedule = new Schedule(ctx.startDate.toEpochDay(), ctx.endDate.toEpochDay(), List.of(ctx.convertedShift));
        persistedSchedule.setId(900L);
        when(ctx.schedulerController.alreadyExistsAnotherSchedule(ctx.startDate, ctx.endDate)).thenReturn(false);
        when(ctx.schedulerController.persistSchedule(any(Schedule.class))).thenReturn(persistedSchedule);

        AiScheduleGenerationOrchestrationService.SelectionResult persistedResult =
                ctx.service.persistSelectedCandidate("standard");

        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.PERSISTED,
                persistedResult.getStatus());
        assertEquals(900L, persistedResult.getScheduleId());
        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.NO_ACTIVE_COMPARISON,
                ctx.service.persistSelectedCandidate("standard").getStatus());
    }

    @Test
    void persistSelectedCandidateWorksForStandardAndAllAiCandidates() {
        List<String> candidateIds = List.of("standard", "ai-empathetic", "ai-efficient", "ai-balanced");

        for (int i = 0; i < candidateIds.size(); i++) {
            String candidateId = candidateIds.get(i);
            TestContext ctx = buildContext();
            when(ctx.schedulerController.alreadyExistsAnotherSchedule(ctx.startDate, ctx.endDate)).thenReturn(false);

            Schedule persistedSchedule = new Schedule(ctx.startDate.toEpochDay(), ctx.endDate.toEpochDay(), List.of(ctx.convertedShift));
            persistedSchedule.setId(1000L + i);
            when(ctx.schedulerController.persistSchedule(any(Schedule.class))).thenReturn(persistedSchedule);

            ctx.service.generateScheduleComparison(ctx.startDate, ctx.endDate);
            AiScheduleGenerationOrchestrationService.SelectionResult result =
                    ctx.service.persistSelectedCandidate(candidateId);

            assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.PERSISTED, result.getStatus());
            assertEquals(1000L + i, result.getScheduleId());
            if ("standard".equals(candidateId)) {
                verify(ctx.aiScheduleConverterService, never()).convert(any());
            } else {
                verify(ctx.aiScheduleConverterService, times(1)).convert(any());
            }
        }
    }

    @Test
    void buildScheduleForCandidateUsesInMemoryStandardAndBuildsAiFromJson() {
        TestContext standardContext = buildContext();
        when(standardContext.schedulerController.alreadyExistsAnotherSchedule(standardContext.startDate, standardContext.endDate))
                .thenReturn(false);
        standardContext.service.generateScheduleComparison(standardContext.startDate, standardContext.endDate);

        Schedule persistedStandard = new Schedule(standardContext.startDate.toEpochDay(),
                standardContext.endDate.toEpochDay(), List.of(standardContext.convertedShift));
        persistedStandard.setId(701L);
        when(standardContext.schedulerController.persistSchedule(any(Schedule.class))).thenReturn(persistedStandard);

        standardContext.service.persistSelectedCandidate("standard");

        ArgumentCaptor<Schedule> standardCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(standardContext.schedulerController).persistSchedule(standardCaptor.capture());
        assertSame(standardContext.transientSchedule, standardCaptor.getValue());
        verify(standardContext.aiScheduleConverterService, never()).convert(any());

        TestContext aiContext = buildContext();
        when(aiContext.schedulerController.alreadyExistsAnotherSchedule(aiContext.startDate, aiContext.endDate)).thenReturn(false);
        aiContext.service.generateScheduleComparison(aiContext.startDate, aiContext.endDate);

        Schedule persistedAi = new Schedule(aiContext.startDate.toEpochDay(), aiContext.endDate.toEpochDay(), List.of(aiContext.convertedShift));
        persistedAi.setId(702L);
        when(aiContext.schedulerController.persistSchedule(any(Schedule.class))).thenReturn(persistedAi);

        aiContext.service.persistSelectedCandidate("ai-empathetic");

        ArgumentCaptor<Schedule> aiCaptor = ArgumentCaptor.forClass(Schedule.class);
        verify(aiContext.schedulerController).persistSchedule(aiCaptor.capture());
        Schedule builtAiSchedule = aiCaptor.getValue();
        assertEquals(aiContext.startDate.toEpochDay(), builtAiSchedule.getStartDate());
        assertEquals(aiContext.endDate.toEpochDay(), builtAiSchedule.getEndDate());
        assertEquals(1, builtAiSchedule.getConcreteShifts().size());
        assertTrue(builtAiSchedule.getConcreteShifts().contains(aiContext.convertedShift));
        verify(aiContext.aiScheduleConverterService, times(1)).convert(any());
    }

    private TestContext buildContext() {
        LocalDate startDate = LocalDate.of(2026, 9, 14);
        LocalDate endDate = LocalDate.of(2026, 9, 15);

        Shift standardShift = mock(Shift.class);
        when(standardShift.getId()).thenReturn(2001L);
        when(standardShift.getQuantityShiftSeniority()).thenReturn(List.of());

        Shift convertedAiShift = mock(Shift.class);
        when(convertedAiShift.getId()).thenReturn(3001L);
        when(convertedAiShift.getQuantityShiftSeniority()).thenReturn(List.of());

        ConcreteShift standardConcreteShift = new ConcreteShift(startDate.toEpochDay(), standardShift);
        ConcreteShift convertedShift = new ConcreteShift(startDate.toEpochDay(), convertedAiShift);
        Schedule transientSchedule = new Schedule(startDate.toEpochDay(), endDate.toEpochDay(), List.of(standardConcreteShift));

        ISchedulerController schedulerController = mock(ISchedulerController.class);
        DoctorDAO doctorDAO = mock(DoctorDAO.class);
        DoctorUffaPriorityDAO doctorUffaPriorityDAO = mock(DoctorUffaPriorityDAO.class);
        DoctorHolidaysDAO doctorHolidaysDAO = mock(DoctorHolidaysDAO.class);
        ScheduleDAO scheduleDAO = mock(ScheduleDAO.class);
        AgentBroker agentBroker = mock(AgentBroker.class);
        AiActiveConstraintResolver aiActiveConstraintResolver = mock(AiActiveConstraintResolver.class);
        DecisionAlgorithmService decisionAlgorithmService = mock(DecisionAlgorithmService.class);
        AiScheduleConverterService aiScheduleConverterService = mock(AiScheduleConverterService.class);
        RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO = mock(RequestRemovalFromConcreteShiftDAO.class);

        AiReschedulingOrchestrationService aiReschedulingOrchestrationService =
                new AiReschedulingOrchestrationService(requestRemovalFromConcreteShiftDAO, aiActiveConstraintResolver);

        when(schedulerController.createScheduleTransient(startDate, endDate)).thenReturn(transientSchedule);
        when(scheduleDAO.save(transientSchedule)).thenReturn(transientSchedule);
        when(requestRemovalFromConcreteShiftDAO.findAllByConcreteShiftDateBetween(startDate.toEpochDay(), endDate.toEpochDay()))
                .thenReturn(List.of());
        when(aiActiveConstraintResolver.resolveWithReport(any(), any(), anyBoolean()))
                .thenReturn(new AiActiveConstraintResolver.ResolveResult(List.of(), 0, 0, 0));
        when(agentBroker.previewTokenBudget(any())).thenReturn(new AiTokenBudgetGuardResult(false, 0, 0, 1000, 10));
        when(aiScheduleConverterService.convert(any())).thenReturn(List.of(convertedShift));
        when(decisionAlgorithmService.selectPreferredWithAudit(any())).thenReturn(new AuditedSelectionResult("standard", List.of()));

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
                new AiHardCoveragePromptBlockBuilder(),
                new ObjectMapper()
        );

        return new TestContext(service,
                schedulerController,
                aiScheduleConverterService,
                transientSchedule,
                convertedShift,
                startDate,
                endDate);
    }

    private static class TestContext {
        private final AiScheduleGenerationOrchestrationService service;
        private final ISchedulerController schedulerController;
        private final AiScheduleConverterService aiScheduleConverterService;
        private final Schedule transientSchedule;
        private final ConcreteShift convertedShift;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private TestContext(AiScheduleGenerationOrchestrationService service,
                            ISchedulerController schedulerController,
                            AiScheduleConverterService aiScheduleConverterService,
                            Schedule transientSchedule,
                            ConcreteShift convertedShift,
                            LocalDate startDate,
                            LocalDate endDate) {
            this.service = service;
            this.schedulerController = schedulerController;
            this.aiScheduleConverterService = aiScheduleConverterService;
            this.transientSchedule = transientSchedule;
            this.convertedShift = convertedShift;
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
