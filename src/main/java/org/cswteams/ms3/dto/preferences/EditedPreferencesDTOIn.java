package org.cswteams.ms3.dto.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
public class EditedPreferencesDTOIn {

    @NotNull
    private final Long doctorId ;

    @NotNull
    private final List<PreferenceDTOIn> remainingPreferences ;

    @NotNull
    private final List<PreferenceDoctorIDDTO> preferencesToDelete ;

    /**
     *
     * @param doctorId the doctor whose preference need to be updated
     * @param remainingPreferences A list of {@link org.cswteams.ms3.dto.preferences.PreferenceDTOIn} representing the updated entities,
     *                             some of them may have the id field set (the modified ones), while others not (the new ones)
     * @param preferencesToDelete A list of {@link org.cswteams.ms3.dto.preferences.PreferenceDoctorIDDTO} representing the preferences that need to be deleted
     */
    public EditedPreferencesDTOIn(
            @JsonProperty("doctorId") Long doctorId,
            @JsonProperty("remainingPreferences") List<PreferenceDTOIn> remainingPreferences,
            @JsonProperty("preferencesToDelete") List<PreferenceDoctorIDDTO> preferencesToDelete) {

        this.doctorId = doctorId ;
        this.remainingPreferences = remainingPreferences ;
        this.preferencesToDelete = preferencesToDelete ;
    }
}
