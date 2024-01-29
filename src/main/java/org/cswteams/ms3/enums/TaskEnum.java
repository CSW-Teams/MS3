package org.cswteams.ms3.enums;

/**
 * This enum describes the possible tasks of which a service can be composed.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#mansioni">Glossary</a>.
 * @see org.cswteams.ms3.entity.Task
 */
public enum TaskEnum {

    CLINIC,

    /**
     * Supervises and responds to current needs. Very important that it does not remain uncovered.
     */
    EMERGENCY,

    /**
     * Manages patients admitted from the emergency room
     */
    WARD,

    /**
     * Manages, sets up or participates in the operating room
     */
    OPERATING_ROOM
}
