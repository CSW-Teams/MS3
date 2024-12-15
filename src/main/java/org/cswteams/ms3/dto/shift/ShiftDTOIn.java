package org.cswteams.ms3.dto.shift;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.cswteams.ms3.utils.validators.admissible_values.AdmissibleValues;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;

@Getter
public class ShiftDTOIn {

    private Long id ;

    @NotNull
    @AdmissibleValues(values = {"MORNING", "AFTERNOON", "NIGHT"})
    private final String timeSlot;

    @NotNull
    @Range(min = 0, max = 23)
    private final Integer startHour ;

    @NotNull
    @Range(min = 0, max = 59)
    private final Integer startMinute ;

    @NotNull
    @Positive
    private final Integer durationMinutes ;

    @NotEmpty
    private final Set<@AdmissibleValues(values = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"}) String> daysOfWeek ;

    @NotEmpty
    private final @Valid MedicalServiceShiftDTO medicalService ;

    @NotEmpty
    private final List<QuantityShiftSeniorityDTO> quantityShiftSeniority;

    @NotNull
    private final List<@Valid AdditionalConstraintShiftDTO> additionalConstraints ;

    /**
     *
     * @param timeSlot A string that represents a name of {@link org.cswteams.ms3.enums.TimeSlot}
     * @param startHour The starting hour of the shift
     * @param startMinute The starting minute of the shift
     * @param durationMinutes The duration of the shift, in minutes
     * @param daysOfWeek A set of Strings that represents names of {@link java.time.DayOfWeek}
     * @param medicalService the DTO representing the medical service associated with the shift
     * @param quantityShiftSeniority A map of strings representing names of {@link org.cswteams.ms3.enums.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     * @param additionalConstraints A list of DTOs representing the additional constraints related to the shift
     */
    public ShiftDTOIn(@JsonProperty("timeSlot") String timeSlot,
                      @JsonProperty("startHour") Integer startHour,
                      @JsonProperty("startMinute") Integer startMinute,
                      @JsonProperty("durationMinutes") Integer durationMinutes,@JsonProperty("daysOfWeek") Set<String> daysOfWeek,
                      @JsonProperty("medicalServices") MedicalServiceShiftDTO medicalService,
                      @JsonProperty("quantityShiftSeniority") List<QuantityShiftSeniorityDTO> quantityShiftSeniority,
                      @JsonProperty("additionalConstraints") List<AdditionalConstraintShiftDTO> additionalConstraints) {
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalService = medicalService;
        this.quantityShiftSeniority = quantityShiftSeniority;
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
     * @param medicalService the DTO representing the medical service associated with the shift
     * @param quantityShiftSeniority A map of strings representing names of {@link org.cswteams.ms3.enums.Seniority} into quantities ;
     *                               <br/>it represents how many doctors of every seniority are needed to cover the shift
     * @param additionalConstraintShiftDTO A list of DTOs representing the additional constraints related to the shift
     */
    public ShiftDTOIn(@JsonProperty("id") Long id, @JsonProperty("timeSlot") String timeSlot,
                      @JsonProperty("startHour") Integer startHour,
                      @JsonProperty("startMinute") Integer startMinute,
                      @JsonProperty("durationMinutes") Integer durationMinutes, @JsonProperty("daysOfWeek") Set<String> daysOfWeek,
                      @JsonProperty("medicalServices") MedicalServiceShiftDTO medicalService,
                      @JsonProperty("quantityShiftSeniority") List<QuantityShiftSeniorityDTO> quantityShiftSeniority,
                      @JsonProperty("additionalConstraints") List<AdditionalConstraintShiftDTO> additionalConstraintShiftDTO) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationMinutes = durationMinutes;
        this.daysOfWeek = daysOfWeek;
        this.medicalService = medicalService;
        this.quantityShiftSeniority = quantityShiftSeniority;
        this.additionalConstraints = additionalConstraintShiftDTO ;
    }
}
