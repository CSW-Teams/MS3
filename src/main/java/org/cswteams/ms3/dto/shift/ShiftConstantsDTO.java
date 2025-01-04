package org.cswteams.ms3.dto.shift;

import lombok.Getter;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.DayOfWeek;

@Getter
public class ShiftConstantsDTO {
    private final Seniority[] seniority = Seniority.values();

    private final DayOfWeek[] daysOfWeek = DayOfWeek.values();

    private final TimeSlot[] timeSlot = TimeSlot.values();

    public ShiftConstantsDTO() {}
}
