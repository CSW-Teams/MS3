package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class ShiftDTOOut {

    private final Long id ;
    private final String timeSlot;
    private final int startHour ;
    private final int startMinute ;
    private final int durationMinutes ;
    private final Set<String> daysOfWeek ;
    private final MedicalServiceShiftDTO medicalService;
    private final List<QuantityShiftSeniorityDTO> quantityShiftSeniority;
    //additionalConstraints


    /**
     *
     * @param id The id of the shift, useful for editing
     * @param timeSlot A string that represents a name of {@link org.cswteams.ms3.enums.TimeSlot}
     * @param startHour The starting hour of the shift
     * @param startMinute The starting minute of the shift
     * @param durationMinutes The duration of the shift, in minutes
     * @param daysOfWeek A set of Strings that represents names of {@link java.time.DayOfWeek}
     * @param medicalService The DTO representing the medical service associated with the shift
     * @param quantityShiftSeniority A map of strings representing names of {@link org.cswteams.ms3.enums.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     */
    public ShiftDTOOut(Long id, String timeSlot, int startHour, int startMinute,
                       int durationMinutes, Set<String> daysOfWeek,
                       MedicalServiceShiftDTO medicalService,
                       List<QuantityShiftSeniorityDTO> quantityShiftSeniority) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalService = medicalService;
        this.quantityShiftSeniority = quantityShiftSeniority;
    }

    @Override
    public String toString() {
        return "ShiftDTOIn{" +
                "id=" + id +
                ", timeSlot='" + timeSlot + '\'' +
                ", startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", durationMinutes=" + durationMinutes +
                ", daysOfWeek=" + daysOfWeek +
                ", medicalService=" + medicalService +
                ", quantityShiftSeniority=" + quantityShiftSeniority +
                '}';
    }
}
