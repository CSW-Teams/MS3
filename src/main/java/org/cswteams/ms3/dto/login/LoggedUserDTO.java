package org.cswteams.ms3.dto.login;

import lombok.Getter;
import org.cswteams.ms3.enums.SystemActor;

import java.util.Set;

/**
 * DTO used in the Login use case (from Service to REST Controller)
 */
@Getter
public class LoggedUserDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private Set<SystemActor> systemActor;


    public LoggedUserDTO(Long id, String name, String lastname, String email, String password, Set<SystemActor> systemActors) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.systemActor = systemActors;
    }
}
