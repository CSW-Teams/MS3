package org.cswteams.ms3.dto.login;

import lombok.Data;
import org.cswteams.ms3.enums.SystemActor;

/**
 * DTO used in the Login use case (from REST Controller to Service)
 */
@Data
public class LoginRequestDTO {

    private String email;
    private String password;
    private SystemActor systemActor;

    public LoginRequestDTO(String email, String password, SystemActor systemActor) {
        this.email = email;
        this.password = password;
        this.systemActor = systemActor;
    }
}
