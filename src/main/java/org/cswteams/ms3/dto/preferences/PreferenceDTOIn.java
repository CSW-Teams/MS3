package org.cswteams.ms3.dto.preferences;

import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.List;
import java.util.Set;

@Getter
public class PreferenceDTOIn {

    private Long id ;
    private int day;
    private int month;
    private int year;
    private Set<TimeSlot> turnKinds;

    /**
     *
     * @param day The day of the month of the preference
     * @param month The month of the preference
     * @param year The year of the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOIn(int day, int month, int year, Set<TimeSlot> turnKinds) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = turnKinds;
    }

    /**
     *
     * @param id The id of the preference, if present
     * @param day The day of the month of the preference
     * @param month The month of the preference
     * @param year The year of the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOIn(Long id, int day, int month, int year, Set<TimeSlot> turnKinds) {
        this(day, month, year, turnKinds);
        this.id = id ;
    }

    public PreferenceDTOIn() {
    }
}
