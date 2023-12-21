package org.cswteams.ms3.dto.login;

import lombok.Data;
import org.cswteams.ms3.enums.SystemActor;

@Data
public class LoggedUserDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private SystemActor systemActor;

    public LoggedUserDTO() {}

    public LoggedUserDTO(Long id, String name, String lastname, String email, String password, SystemActor systemActor) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.systemActor = systemActor;
    }
}
