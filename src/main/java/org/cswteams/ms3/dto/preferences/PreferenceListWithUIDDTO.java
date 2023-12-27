package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

import java.util.List;

@Getter
public class PreferenceListWithUIDDTO {

    private final Long doctorId;
    private final List<PreferenceDTOIn> dto ;

    public PreferenceListWithUIDDTO(Long doctorId, List<PreferenceDTOIn> dto) {
        this.doctorId = doctorId;
        this.dto = dto ;
    }
}
