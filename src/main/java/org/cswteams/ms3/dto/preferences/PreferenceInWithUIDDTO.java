package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

@Getter
public class PreferenceInWithUIDDTO {

    private final Long doctorId;
    private final PreferenceDTOIn dto ;

    public PreferenceInWithUIDDTO(Long doctorId, PreferenceDTOIn dto) {
        this.doctorId = doctorId;
        this.dto = dto ;
    }
}
