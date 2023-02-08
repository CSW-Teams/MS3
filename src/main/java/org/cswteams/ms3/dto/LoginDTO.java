package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class LoginDTO {

    private String username;
    private String password;

    public LoginDTO() { }

    public LoginDTO(String username, String Password) {
        this.username = username;
        this.password = password;
    }



}
