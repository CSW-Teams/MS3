package org.cswteams.ms3.dto.systemactor;

import lombok.Getter;

import java.util.Set;

// DTO from client to server as response of api request
@Getter
public class AllSystemActorsDTO {
    private final Set<String> systemActors;

    /**
     * This DTO holds the information about all the existing actors in the system
     * @param systemActors The set of string representing all the actors
     */
    public AllSystemActorsDTO(Set<String> systemActors){
        this.systemActors = systemActors;
    }
}
