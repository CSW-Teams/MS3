package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
public class ShiftDTOOut {

    private final Long id ;
    private final String timeslot ;
    private final int startHour ;
    private final int startMinute ;
    private final int durationMinutes ;
    private final Set<String> daysOfWeek ;
    private final List<MedicalServiceShiftDTO> medicalServices ;
    private final HashMap<String, Integer> quantityshiftseniority ;
    //additionalConstraints


    /**
     *
     * @param id The id of the shift, useful for editing
     * @param timeslot A string that represents a name of {@link org.cswteams.ms3.enums.TimeSlot}
     * @param startHour The starting hour of the shift
     * @param startMinute The starting minute of the shift
     * @param durationMinutes The duration of the shift, in minutes
     * @param daysOfWeek A set of Strings that represents names of {@link java.time.DayOfWeek}
     * @param medicalServices A list of DTOs representing the medical services associated with the shift
     * @param quantityshiftseniority A map of strings representing names of {@link org.cswteams.ms3.entity.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     */
    public ShiftDTOOut(Long id, String timeslot, int startHour, int startMinute,
                       int durationMinutes, Set<String> daysOfWeek,
                       List<MedicalServiceShiftDTO> medicalServices,
                       HashMap<String, Integer> quantityshiftseniority) {
        this.id = id;
        this.timeslot = timeslot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalServices = medicalServices;
        this.quantityshiftseniority = quantityshiftseniority;
    }
}
