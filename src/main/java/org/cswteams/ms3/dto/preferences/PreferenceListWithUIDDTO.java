package org.cswteams.ms3.dto.preferences;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Getter
public class PreferenceListWithUIDDTO {

    @NotNull
    @PositiveOrZero
    private final Long doctorId;

    @NotEmpty
    private final List<@Valid PreferenceDTOIn> dto ;

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
