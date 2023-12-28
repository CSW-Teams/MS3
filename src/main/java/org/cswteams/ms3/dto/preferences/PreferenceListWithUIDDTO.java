package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

import java.util.List;

@Getter
public class PreferenceListWithUIDDTO {

    private final Long doctorId;
    private final List<PreferenceDTOIn> dto ;

    /**
     *
     * @param doctorId The id of the doctor to whom the event is related
     * @param dto A list of DTOs representing the preference to insert
     */
    public PreferenceListWithUIDDTO(Long doctorId, List<PreferenceDTOIn> dto) {
        this.doctorId = doctorId;
        this.dto = dto ;
    }
}
