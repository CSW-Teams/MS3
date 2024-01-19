package org.cswteams.ms3.dto.systemactor;

import lombok.Getter;

// DTO sent in DELETE and POST request from client to server to delete a specialization of a doctor
@Getter
public class UserSystemActorDTO {
    private final Long userID;
    private final String systemActor;

    /**
     * This DTO brings the information to the backend needed to delete the specialization of a doctor
     * @param userID The ID with which the doctor is identified in the DB
     * @param systemActor The string representing the role of the user in the MS3 system
     */
    public UserSystemActorDTO(Long userID, String systemActor) {
        this.userID = userID;
        this.systemActor = systemActor;
    }

}
