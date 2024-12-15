package org.cswteams.ms3.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserProfileDTO extends UserDetailsDTO {
    private long id;

    public UpdateUserProfileDTO(long id, String name, String lastname, String email, String birthday, String seniority) {
        super(name, lastname, email, birthday, seniority);
        this.id = id;
    }
}