package org.cswteams.ms3.control.controllerscheduler.utils;

import java.time.LocalDate;

/**
 * Utility enumeration containing some useful dates often used in some tests.
 */
public enum TestDatesEnum {

    /**
     * A date in the past, for something starting.
     */
    PREVIOUS_START(LocalDate.now().minusYears(10).withMonth(1).withDayOfMonth(1)),

    /**
     * A date in the past, for something ending.
     */
    PREVIOUS_END(LocalDate.now().minusYears(10).withMonth(1).withDayOfMonth(5)),

    /**
     * A date in the future, for something starting.
     */
    FUTURE_START(LocalDate.now().plusYears(10).withMonth(1).withDayOfMonth(1)),

    /**
     * A date in the future, for something ending.
     */
    FUTURE_END(LocalDate.now().plusYears(10).withMonth(1).withDayOfMonth(5)),

    /**
     * Today's date.
     */
    TODAY(LocalDate.now());

    TestDatesEnum(LocalDate date) {
        this.date = date;
    }

    /**
     * Internal state.
     */
    private final LocalDate date;

    public LocalDate getDate() {
        return date;
    }
}
