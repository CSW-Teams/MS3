package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
public class ShiftDTOIn {

    private Long id ;
    private final String timeSlot;
    private final int startHour ;
    private final int startMinute ;
    private final int durationMinutes ;
    private final Set<String> daysOfWeek ;
    private final List<MedicalServiceShiftDTO> medicalServices ;
    private final HashMap<String, Integer> quantityshiftSeniority ;
    private final List<AdditionalConstraintShiftDTO> additionalConstraints ;

    /**
     *
     * @param timeSlot A string that represents a name of {@link org.cswteams.ms3.enums.TimeSlot}
     * @param startHour The starting hour of the shift
     * @param startMinute The starting minute of the shift
     * @param durationMinutes The duration of the shift, in minutes
     * @param daysOfWeek A set of Strings that represents names of {@link java.time.DayOfWeek}
     * @param medicalServices A list of DTOs representing the medical services associated with the shift
     * @param quantityshiftseniority A map of strings representing names of {@link org.cswteams.ms3.entity.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     * @param additionalConstraints A list of DTOs representing the additional constraints related to the shift
     */
    public ShiftDTOIn(String timeSlot, int startHour, int startMinute,
                      int durationMinutes, Set<String> daysOfWeek,
                      List<MedicalServiceShiftDTO> medicalServices,
                      HashMap<String, Integer> quantityshiftseniority,
                      List<AdditionalConstraintShiftDTO> additionalConstraints) {
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalServices = medicalServices;
        this.quantityshiftSeniority = quantityshiftseniority;
        this.additionalConstraints = additionalConstraints;
    }

    /**
     *
     * @param id The id of the shift, useful for editing
     * @param timeSlot A string that represents a name of {@link org.cswteams.ms3.enums.TimeSlot}
     * @param startHour The starting hour of the shift
     * @param startMinute The starting minute of the shift
     * @param durationMinutes The duration of the shift, in minutes
     * @param daysOfWeek A set of Strings that represents names of {@link java.time.DayOfWeek}
     * @param medicalServices A list of DTOs representing the medical services associated with the shift
     * @param quantityshiftseniority A map of strings representing names of {@link org.cswteams.ms3.entity.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     * @param additionalConstraintShiftDTO A list of DTOs representing the additional constraints related to the shift
     */
    public ShiftDTOIn(Long id, String timeSlot, int startHour, int startMinute,
                      int durationMinutes, Set<String> daysOfWeek,
                      List<MedicalServiceShiftDTO> medicalServices,
                      HashMap<String, Integer> quantityshiftseniority,
                      List<AdditionalConstraintShiftDTO> additionalConstraintShiftDTO) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalServices = medicalServices;
        this.quantityshiftSeniority = quantityshiftseniority;
        this.additionalConstraints = additionalConstraintShiftDTO ;
    }
}
