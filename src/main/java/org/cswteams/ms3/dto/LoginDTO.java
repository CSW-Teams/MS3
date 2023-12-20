package org.cswteams.ms3.dto;

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
