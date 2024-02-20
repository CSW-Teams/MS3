package org.cswteams.ms3.dto.specializations;

import lombok.Getter;

import java.util.Set;

@Getter
// GET request from server to client
public class SpecializationDTO{
    private final Set<String> specializations;

    /**
     * This DTo holds the responsibility to show all the specializations saved in the system
     * @param specializations List of string representing the specializations
     */
    public SpecializationDTO(Set<String> specializations){
        this.specializations = specializations;
    }
}