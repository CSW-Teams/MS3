package org.cswteams.ms3.dto.preferences;

import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class PreferenceDTOOut {

    private Long preferenceId;
    private int day;
    private int month;
    private int year;
    private Set<String> turnKinds;

    /**
     *
     * @param day The day of the month relative to the preference
     * @param month The month relative to the preference
     * @param year The year relative to the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOOut(int day, int month, int year, Set<String> turnKinds) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = turnKinds;
    }

    /**
     *
     * @param day The day of the month relative to the preference
     * @param month The month relative to the preference
     * @param year The year relative to the preference
     */
    public PreferenceDTOOut(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = new HashSet<>();
    }

    /**
     *
     * @param preferenceId The id of the preference
     * @param day The day of the month relative to the preference
     * @param month The month relative to the preference
     * @param year The year relative to the preference
     * @param turnKinds A list of shift time slots relative to the preference
     */
    public PreferenceDTOOut(Long preferenceId, int day, int month, int year, Set<String> turnKinds) {
        this(day, month, year, turnKinds);
        this.preferenceId = preferenceId;
    }

    public PreferenceDTOOut(){}
}
