package org.cswteams.ms3.dto.preferences;

import lombok.Getter;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.List;

@Getter
public class PreferenceDTOIn {

    private final int day;
    private final int month;
    private final int year;
    private final List<TimeSlot> turnKinds;

    public PreferenceDTOIn(int day, int month, int year, List<TimeSlot> turnKinds) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.turnKinds = turnKinds;
    }
}
