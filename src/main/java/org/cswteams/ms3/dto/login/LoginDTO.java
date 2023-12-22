package org.cswteams.ms3.dto.login;

import lombok.Data;

@Data
public class LoginDTO {

    private String email;
    private String password;

    public LoginDTO() { }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }



}
