package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.ai.broker.AiPromptTemplate;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiHardCoveragePromptBlockBuilderTest {

    private final AiHardCoveragePromptBlockBuilder builder = new AiHardCoveragePromptBlockBuilder();

    @Test
    void buildHardCoverageRequirementsBlockAggregatesRequirementsAndSortsRowsDeterministically() {
        LocalDate day1 = LocalDate.of(2026, 9, 14);
        LocalDate day2 = LocalDate.of(2026, 9, 15);

        ConcreteShift day2Morning = new ConcreteShift(day2.toEpochDay(), makeShift(1002L, TimeSlot.MORNING,
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 2)));
        ConcreteShift day1Afternoon = new ConcreteShift(day1.toEpochDay(), makeShift(1003L, TimeSlot.AFTERNOON,
                Map.of(Seniority.SPECIALIST_SENIOR, 1)));
        ConcreteShift day1Morning = new ConcreteShift(day1.toEpochDay(), makeShift(1001L, TimeSlot.MORNING,
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_SENIOR, 2)));

        String block = builder.buildHardCoverageRequirementsBlock(List.of(day2Morning, day1Afternoon, day1Morning));

        assertEquals(
                "hard_coverage_requirements[3]{shift_id,structured,specialist_junior,specialist_senior,total}:\n"
                        + "S_1001_20260914,1,0,2,3\n"
                        + "S_1003_20260914,0,0,1,1\n"
                        + "S_1002_20260915,1,2,0,3\n"
                        + "hard_coverage_checklist[3]:\n"
                        + "- ON_DUTY and ON_CALL each must satisfy every per-shift/per-role minimum.\n"
                        + "- ON_CALL minimums are identical to ON_DUTY minimums.\n"
                        + "- For a given shift_id, one doctor_id cannot appear in both ON_DUTY and ON_CALL.\n",
                block
        );
    }

    @Test
    void buildHardCoverageRequirementsBlockHandlesMissingRequirementsAsZeroes() {
        LocalDate day = LocalDate.of(2026, 9, 14);
        Shift shift = new Shift(
                1001L,
                TimeSlot.MORNING,
                LocalTime.of(8, 0),
                Duration.ofHours(6),
                Set.of(DayOfWeek.MONDAY),
                new MedicalService(List.of(new Task(TaskEnum.CLINIC)), "Ward"),
                List.of(),
                List.of()
        );
        ConcreteShift concreteShift = new ConcreteShift(day.toEpochDay(), shift);

        String block = builder.buildHardCoverageRequirementsBlock(List.of(concreteShift));

        assertEquals(
                "hard_coverage_requirements[1]{shift_id,structured,specialist_junior,specialist_senior,total}:\n"
                        + "S_1001_20260914,0,0,0,0\n"
                        + "hard_coverage_checklist[3]:\n"
                        + "- ON_DUTY and ON_CALL each must satisfy every per-shift/per-role minimum.\n"
                        + "- ON_CALL minimums are identical to ON_DUTY minimums.\n"
                        + "- For a given shift_id, one doctor_id cannot appear in both ON_DUTY and ON_CALL.\n",
                block
        );
    }

    @Test
    void hardCoverageSchemaMatchesSystemPromptContract() {
        LocalDate day = LocalDate.of(2026, 9, 16);
        ConcreteShift concreteShift = new ConcreteShift(day.toEpochDay(), makeShift(
                2001L,
                TimeSlot.NIGHT,
                Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_JUNIOR, 1, Seniority.SPECIALIST_SENIOR, 1)
        ));

        String block = builder.buildHardCoverageRequirementsBlock(List.of(concreteShift));
        String systemPrompt = AiPromptTemplate.systemPrompt();

        String expectedHeader = "hard_coverage_requirements[1]{shift_id,structured,specialist_junior,specialist_senior,total}:\n";
        assertTrue(systemPrompt.contains("hard_coverage_requirements[n]{shift_id,structured,specialist_junior,specialist_senior,total}"));
        assertTrue(systemPrompt.contains("Every minimum value in `hard_coverage_requirements` (`structured`, `specialist_junior`, `specialist_senior`, and `total`) is mandatory"));
        assertTrue(block.startsWith(expectedHeader));
        assertTrue(block.contains("S_2001_20260916,1,1,1,3\n"));
        assertTrue(block.contains("hard_coverage_checklist[3]:"));
        assertTrue(block.contains("ON_DUTY and ON_CALL each must satisfy every per-shift/per-role minimum."));
    }

    @Test
    void hardCoverageRequirementsHeaderAlwaysIncludesAllMandatoryColumns() {
        String block = builder.buildHardCoverageRequirementsBlock(List.of());

        assertEquals(
                "hard_coverage_requirements[0]{shift_id,structured,specialist_junior,specialist_senior,total}:\n"
                        + "hard_coverage_checklist[3]:\n"
                        + "- ON_DUTY and ON_CALL each must satisfy every per-shift/per-role minimum.\n"
                        + "- ON_CALL minimums are identical to ON_DUTY minimums.\n"
                        + "- For a given shift_id, one doctor_id cannot appear in both ON_DUTY and ON_CALL.\n",
                block
        );
    }

    private Shift makeShift(Long id, TimeSlot timeSlot, Map<Seniority, Integer> seniorityMap) {
        Task task = new Task(TaskEnum.CLINIC);
        MedicalService service = new MedicalService(List.of(task), "Ward");
        QuantityShiftSeniority quantity = new QuantityShiftSeniority(seniorityMap, task);
        return new Shift(
                id,
                timeSlot,
                LocalTime.of(8, 0),
                Duration.ofHours(6),
                Set.of(DayOfWeek.MONDAY),
                service,
                List.of(quantity),
                List.of()
        );
    }
}
