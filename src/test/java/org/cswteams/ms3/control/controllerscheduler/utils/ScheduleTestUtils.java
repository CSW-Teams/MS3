package org.cswteams.ms3.control.controllerscheduler.utils;

import org.cswteams.ms3.entity.Schedule;

/**
 * Some utility functions for various <code>Schedule</code> validity checks.
 */
public abstract class ScheduleTestUtils {

    /**
     * Check if the two <code>Schedule</code>s <code>a</code> and <code>b</code> overlap (by at least one day)
     *
     * @param a Schedule
     * @param b Schedule
     * @return <code>true</code> if the schedules <code>a</code> and <code>b</code> overlap, <code>false</code> elsewhere.
     */
    public static boolean overlapCheck(Schedule a, Schedule b) {
        return !(b.getEndDate().isBefore(a.getStartDate()) || b.getStartDate().isAfter(a.getEndDate()));
    }

    /**
     * Check if the two <code>Schedule</code>s <code>a</code> and <code>b</code> overlap perfectly (they start and end on the same dates)
     *
     * @param a Schedule
     * @param b Schedule
     * @return <code>true</code> if the schedules <code>a</code> and <code>b</code> overlap perfectly, <code>false</code> elsewhere.
     */
    public static boolean perfectOverlapCheck(Schedule a, Schedule b) {
        return a.getStartDate().equals(b.getStartDate())
                && a.getEndDate().equals(b.getEndDate());
    }

    /**
     * Check if the two <code>Schedule</code>s <code>a</code> and <code>b</code> are the same (by <code>id</code>).
     *
     * @param a Schedule
     * @param b Schedule
     * @return <code>true</code> if the schedules <code>a</code> and <code>b</code> have the same id, <code>false</code> elsewhere.
     */
    public static boolean isSameScheduleCheck(Schedule a, Schedule b) {
        return a.getId().equals(b.getId());
    }

    /**
     * Check if an overlap between two <code>Schedule</code>s could be allowed, that is:
     * - they do NOT overlap,
     * *** OR ***
     * - if they do, we allow it, because they are the same <code>Schedule</code> (they have the same <code>id</code>).
     *
     * @param a Schedule
     * @param b Schedule
     * @return <code>true</code> if <code>a</code> and <code>b</code> do NOT overlap or, if they do, they are the same schedule.
     */
    public static boolean overlapAllowanceCheck(Schedule a, Schedule b) {
        if (!overlapCheck(a, b)) {
            return true;
        } else {
            return perfectOverlapCheck(a, b) && isSameScheduleCheck(a, b);
        }

    }
}
