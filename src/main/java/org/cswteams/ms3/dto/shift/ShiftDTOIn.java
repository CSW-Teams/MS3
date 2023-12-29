package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Getter
public class ShiftDTOIn {

    private Long id ;
    private String timeSlot;
    private int startHour ;
    private int startMinute ;
    private int durationMinutes ;
    private Set<String> daysOfWeek ;
    private List<MedicalServiceShiftDTO> medicalServices ;
    private HashMap<String, Integer> quantityShiftSeniority;
    private List<AdditionalConstraintShiftDTO> additionalConstraints = List.of() ;

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
        this.quantityShiftSeniority = quantityshiftseniority;
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
        this.quantityShiftSeniority = quantityshiftseniority;
        this.additionalConstraints = additionalConstraintShiftDTO ;
    }

    public ShiftDTOIn() {}
}
