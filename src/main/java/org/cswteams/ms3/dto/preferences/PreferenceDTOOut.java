package org.cswteams.ms3.dto.preferences;

import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PreferenceDTOOut {

    private Long preferenceId;
    private int day;
    private int month;
    private int year;
    private List<TimeSlot> turnKinds;

    public PreferenceDTOOut(int day, int month, int year, List<TimeSlot> turnKinds) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = turnKinds;
    }


    public PreferenceDTOOut(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = new ArrayList<>();
    }

    public PreferenceDTOOut(Long preferenceId, int day, int month, int year, List<TimeSlot> turnKinds) {
        this(day, month, year, turnKinds);
        this.preferenceId = preferenceId;
    }

    public PreferenceDTOOut(){}
}
