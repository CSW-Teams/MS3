package org.cswteams.ms3.dto.systemactor;

import lombok.Getter;

import java.util.Set;

// DTO sent in DELETE and POST request from client to server to delete a specialization of a doctor
@Getter
public class UserSystemActorsDTO {
    private final Long userID;
    private final Set<String> systemActors;

    /**
     * This DTO brings the information to the backend needed to delete the specialization of a doctor
     * @param userID The ID with which the doctor is identified in the DB
     * @param systemActors The list of strings representing the role of the user in the MS3 system
     */
    public UserSystemActorsDTO(Long userID, Set<String> systemActors) {
        this.userID = userID;
        this.systemActors = systemActors;
    }

}