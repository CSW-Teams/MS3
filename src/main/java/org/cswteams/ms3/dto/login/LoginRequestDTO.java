package org.cswteams.ms3.dto.login;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
    private String turnstileToken; // Il nostro campo nuovo
    private String twoFactorCode;

    public LoginRequestDTO() {}

    // 2. Costruttore COMPLETO
    public LoginRequestDTO(String email, String password, String turnstileToken) {
        this.email = email;
        this.password = password;
        this.turnstileToken = turnstileToken;
    }

    public LoginRequestDTO(String email, String password, String turnstileToken, String twoFactorCode) {
        this.email = email;
        this.password = password;
        this.turnstileToken = turnstileToken;
        this.twoFactorCode = twoFactorCode;
    }

    // 3. Costruttore compatibile con i vecchi test
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.turnstileToken = null;
    }
}
