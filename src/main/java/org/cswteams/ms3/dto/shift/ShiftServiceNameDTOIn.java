package org.cswteams.ms3.dto.shift;

import lombok.Getter;

@Getter
public class ShiftServiceNameDTOIn {

    private final String serviceLabel ;

    /**
     *
     * @param serviceLabel the name of the service used for research
     */
    public ShiftServiceNameDTOIn(String serviceLabel) {
        this.serviceLabel = serviceLabel;
    }
}
