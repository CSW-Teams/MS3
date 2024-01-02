package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
public class PreferenceDoctorIDDTO {

    @NotNull
    @PositiveOrZero
    private final Long doctorId ;

    @NotNull
    @PositiveOrZero
    private final Long preferenceId ;

    /**
     * @param doctorId The id of the doctor that wants a preference deleted
     * @param preferenceId The id of the preference to delete
     */
    public PreferenceDoctorIDDTO(Long doctorId, Long preferenceId) {
        this.doctorId = doctorId;
        this.preferenceId = preferenceId;
    }
}
