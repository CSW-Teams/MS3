package org.cswteams.ms3.dto.login;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
    private String turnstileToken; // Il nostro campo nuovo

    public LoginRequestDTO() {}

    // 2. Costruttore COMPLETO
    public LoginRequestDTO(String email, String password, String turnstileToken) {
        this.email = email;
        this.password = password;
        this.turnstileToken = turnstileToken;
    }

    // 3. Costruttore COMPATIBILITÀ (Per i vecchi test)
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.turnstileToken = null;
    }
}