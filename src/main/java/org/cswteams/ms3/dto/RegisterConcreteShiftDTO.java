package org.cswteams.ms3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.utils.admissible_values.AdmissibleValues;
import org.cswteams.ms3.utils.date_parts.DateParts;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@DateParts(day = "day", month = "month", year = "year")
public class RegisterConcreteShiftDTO {

    @NotNull
    private Integer day;
    @NotNull
    private Integer month;
    @NotNull
    private Integer year;

    @NotNull
    @AdmissibleValues(values = {"NIGHT", "AFTERNOON", "MORNING"})
    private String timeSlot;

    @NotEmpty
    private Set<@Valid UserCreationDTO> onDutyDoctors;
    @NotEmpty
    private Set<@Valid UserCreationDTO> onCallDoctors;
    @NotEmpty
    private Set<@Valid MedicalServiceDTO> services;

    @NotNull
    private boolean forced;

    //TODO: to be removed?
    @NotNull
    @AdmissibleValues(values = "CLINIC, EMERGENCY, WARD, OPERATING_ROOM")
    private String mansione;

    @NotNull
    @Valid
    private MedicalServiceDTO servizio;

    public RegisterConcreteShiftDTO(
            @JsonProperty("day") Integer day,
            @JsonProperty("month") Integer month,
            @JsonProperty("year") Integer year,
            @JsonProperty("timeSlot") String timeSlot,
            @JsonProperty("onDutyDoctors") Set<@Valid UserCreationDTO> onDutyDoctors,
            @JsonProperty("onCallDoctors") Set<@Valid UserCreationDTO> onCallDoctors,
            @JsonProperty("services") Set<@Valid MedicalServiceDTO> services,
            @JsonProperty("forced") boolean forced,
            @JsonProperty("mansione") String mansione,
            @JsonProperty("servizio") MedicalServiceDTO servizio) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.timeSlot = timeSlot;
        this.onDutyDoctors = onDutyDoctors;
        this.onCallDoctors = onCallDoctors;
        this.services = services;
        this.forced = forced;
        this.mansione = mansione;
        this.servizio = servizio;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }


}
