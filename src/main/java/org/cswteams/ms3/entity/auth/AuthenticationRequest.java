package org.cswteams.ms3.entity.auth;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AuthenticationRequest implements Serializable {
    private String username;
    private String password;
    private String role;

    // Default constructor for JSON parsing
    public AuthenticationRequest() {}

    public AuthenticationRequest(String username, String password, String role) {
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
    }
}
