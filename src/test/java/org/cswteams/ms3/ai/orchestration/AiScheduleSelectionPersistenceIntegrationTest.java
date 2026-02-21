package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.AbstractMultiTenantIntegrationTest;
import org.cswteams.ms3.ai.comparison.domain.DecisionMetricValues;
import org.cswteams.ms3.ai.comparison.domain.ScheduleCandidateType;
import org.cswteams.ms3.config.multitenancy.SchemasInitializer;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.entity.Schedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SchemasInitializer.class)
@ActiveProfiles("test")
@Transactional
class AiScheduleSelectionPersistenceIntegrationTest extends AbstractMultiTenantIntegrationTest {

    private static final Path WELL_KNOWN_SIMULATED_SCHEDULE_PATH =
            Paths.get("src", "test", "resources", "ai", "simulated-schedule.json");

    @Autowired
    private AiScheduleGenerationOrchestrationService orchestrationService;


    @Autowired
    private ScheduleDAO scheduleDAO;

    @BeforeEach
    void setTenantContext() {
        this.setUpTenantForTx();
    }

    @Test
    void persistSelectedCandidate_readsWellKnownJsonAndPersistsScheduleWithRollback() throws Exception {
        String rawJson = new String(Files.readAllBytes(WELL_KNOWN_SIMULATED_SCHEDULE_PATH), StandardCharsets.UTF_8);

        LocalDate startDate = LocalDate.of(2026, 1, 11);
        LocalDate endDate = LocalDate.of(2026, 1, 11);
        deleteExistingSchedulesForRange(startDate, endDate);

        String candidateId = "ai-balanced";
        Object candidateData = buildCandidateData(candidateId, ScheduleCandidateType.BALANCED, rawJson);
        Object transientState = buildTransientState(startDate, endDate, Map.of(candidateId, candidateData));
        setTransientState(transientState);

        AiScheduleGenerationOrchestrationService.SelectionResult result =
                orchestrationService.persistSelectedCandidate(candidateId);

        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.PERSISTED, result.getStatus());
        assertNotNull(result.getScheduleId());

        Optional<Schedule> persistedSchedule = scheduleDAO.findById(result.getScheduleId());
        assertTrue(persistedSchedule.isPresent());
        assertEquals(startDate.toEpochDay(), persistedSchedule.get().getStartDate());
        assertEquals(endDate.toEpochDay(), persistedSchedule.get().getEndDate());
        assertFalse(persistedSchedule.get().getConcreteShifts().isEmpty());

        assertEquals(AiScheduleGenerationOrchestrationService.SelectionResult.Status.NO_ACTIVE_COMPARISON,
                orchestrationService.persistSelectedCandidate(candidateId).getStatus());
    }

    private void deleteExistingSchedulesForRange(LocalDate startDate, LocalDate endDate) {
        List<Schedule> existing = scheduleDAO.findAll();
        for (Schedule schedule : existing) {
            if (schedule.getStartDate() == startDate.toEpochDay() && schedule.getEndDate() == endDate.toEpochDay()) {
                scheduleDAO.delete(schedule);
            }
        }
    }

    private Object buildCandidateData(String candidateId,
                                      ScheduleCandidateType candidateType,
                                      String rawJson) throws Exception {
        Class<?> validationClass = Class.forName(
                "org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService$CandidateValidationData");
        Method validFactory = validationClass.getDeclaredMethod("valid");
        validFactory.setAccessible(true);
        Object validationData = validFactory.invoke(null);

        Class<?> candidateClass = Class.forName(
                "org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService$CandidateData");
        Constructor<?> constructor = candidateClass.getDeclaredConstructor(
                String.class,
                Long.class,
                ScheduleCandidateType.class,
                String.class,
                DecisionMetricValues.class,
                Schedule.class,
                validationClass
        );
        constructor.setAccessible(true);

        return constructor.newInstance(
                candidateId,
                null,
                candidateType,
                rawJson,
                new DecisionMetricValues(1.0, 1.0, 1.0, 1.0, 1.0),
                null,
                validationData
        );
    }

    private Object buildTransientState(LocalDate startDate,
                                       LocalDate endDate,
                                       Map<String, Object> candidateById) throws Exception {
        Class<?> transientStateClass = Class.forName(
                "org.cswteams.ms3.ai.orchestration.AiScheduleGenerationOrchestrationService$TransientComparisonState");

        Map<String, Object> orderedCandidates = new LinkedHashMap<>(candidateById);
        Constructor<?> constructor = transientStateClass.getDeclaredConstructor(
                LocalDate.class,
                LocalDate.class,
                Map.class,
                Class.forName("org.cswteams.ms3.ai.comparison.dto.AiScheduleComparisonResponseDto")
        );
        constructor.setAccessible(true);
        return constructor.newInstance(startDate, endDate, orderedCandidates, null);
    }

    private void setTransientState(Object transientState) throws Exception {
        Field stateField = AiScheduleGenerationOrchestrationService.class.getDeclaredField("transientComparisonState");
        stateField.setAccessible(true);
        @SuppressWarnings("unchecked")
        AtomicReference<Object> stateRef = (AtomicReference<Object>) stateField.get(orchestrationService);
        stateRef.set(transientState);
    }
}
