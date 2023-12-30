package org.cswteams.ms3.dto.shift;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class ShiftServiceNameDTOIn {

    @NotNull
    private final String serviceLabel ;

    /**
     *
     * @param serviceLabel the name of the service used for research
     */
    public ShiftServiceNameDTOIn(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }
}
