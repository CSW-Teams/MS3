package org.cswteams.ms3.multitenancyapp.dto.login;

import lombok.Getter;
import org.cswteams.ms3.multitenancyapp.entity.Hospital;

import java.util.Set;

@Getter
public class LoginResponseDTO {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final Set<Hospital> systemHospitals;

    private final String jwt;

    public LoginResponseDTO(CustomUserDetails customUserDetails, String jwt) {
        this.id = customUserDetails.getId();
        this.name = customUserDetails.getName();
        this.lastname = customUserDetails.getLastname();
        this.email = customUserDetails.getEmail();
        this.systemHospitals = customUserDetails.getSystemHospitals();

        this.jwt = jwt;
    }
}
