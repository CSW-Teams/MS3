package org.cswteams.ms3.dto.login;

import lombok.Getter;
import org.cswteams.ms3.enums.SystemActor;

import java.util.Set;

@Getter
public class LoginResponseDTO {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final Set<SystemActor> systemActors;
    private final String tenant;

    private final String jwt;

    public LoginResponseDTO(CustomUserDetails customUserDetails, String jwt) {
        this.id = customUserDetails.getId();
        this.name = customUserDetails.getName();
        this.lastname = customUserDetails.getLastname();
        this.email = customUserDetails.getEmail();
        this.systemActors = customUserDetails.getSystemActors();
        this.tenant = customUserDetails.getTenant();

        this.jwt = jwt;
    }
}
