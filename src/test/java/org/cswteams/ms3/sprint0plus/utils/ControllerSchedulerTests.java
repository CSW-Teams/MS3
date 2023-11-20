package org.cswteams.ms3.sprint0plus.utils;

import org.cswteams.ms3.control.scheduler.ControllerScheduler;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

import static org.cswteams.ms3.sprint0plus.utils.ControllerSchedulerTests.TestDatesEnum.*;

public class ControllerSchedulerTests extends TestEnv{
    @Autowired
    protected ControllerScheduler instance;


    protected static Map<TestDatesEnum, LocalDate> testDates;

    public enum TestDatesEnum {
        PREVIOUS_START,
        PREVIOUS_END,
        FUTURE_START,
        FUTURE_END,
        TODAY

    }

    public ControllerSchedulerTests() {
        testDates = new EnumMap<>(TestDatesEnum.class);
        testDates.put(PREVIOUS_START, LocalDate.of(2023, 1, 1));
        testDates.put(PREVIOUS_END, LocalDate.of(2023, 1, 7));
        testDates.put(FUTURE_START, LocalDate.of(2025, 1, 1));
        testDates.put(FUTURE_END, LocalDate.of(2025, 1, 7));
        testDates.put(TODAY, LocalDate.now());
    }


}
