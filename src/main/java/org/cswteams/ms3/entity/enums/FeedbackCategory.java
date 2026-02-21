package org.cswteams.ms3.entity.enums;

/**
 * Represents the category of a feedback, indicating the specific type of issue or conflict reported by a user.
 * This helps in classifying and analyzing feedback to improve the scheduling system.
 */
public enum FeedbackCategory {
    /**
     * Indicates that a user has been assigned the same weekday too frequently, leading to a repetitive and unbalanced schedule.
     * e.g., "I worked every Monday this month."
     */
    REPEATED_WEEKDAY,

    /**
     * Indicates that a user has been assigned shifts in the same time slot (e.g., morning, afternoon, night) too often,
     * lacking variety in their work schedule.
     * e.g., "All my shifts were at night."
     */
    REPEATED_TIME_SLOT,

    /**
     * Indicates a conflict where a user is assigned shifts that are too close to each other, without adequate rest time in between.
     * e.g., "I had a night shift followed by a morning shift the next day."
     */
    CONSECUTIVE_SHIFTS,

    /**
     * Indicates dissatisfaction with the total number of hours or shifts assigned, either too many (overload) or too few (underload).
     * e.g., "I was assigned too many shifts this period."
     */
    WORKLOAD_IMBALANCE,

    /**
     * Indicates a conflict with a user's personal preferences or unavailability that was not respected.
     * e.g., "I had requested this day off for an appointment."
     */
    PREFERENCE_VIOLATION,

    /**
     * A general category for feedback that does not fit into any of the more specific categories.
     */
    OTHER
}
