package org.cswteams.ms3.multitenancyapp.dto.login;

import lombok.Data;

/**
 * DTO used in the Login use case (from REST Controller to Service)
 */
@Data
public class LoginRequestDTO {

    private String email;
    private String password;
    private String tenant;

    public LoginRequestDTO(String email, String password, String tenant) {
        this.email = email;
        this.password = password;
        this.tenant = tenant;
    }
}
