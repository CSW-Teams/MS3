package org.cswteams.ms3.dto.login;

import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private final String jwt;

    public LoginResponseDTO(String jwt) {
        this.jwt = jwt;
    }
}
