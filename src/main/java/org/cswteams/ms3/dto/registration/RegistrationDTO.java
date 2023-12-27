package org.cswteams.ms3.dto.registration;

import lombok.Data;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO used in the Registration use case (from REST Controller to Service)
 */
@Data
public class RegistrationDTO {

    private String name;
    private String lastname;
    private LocalDate birthday;
    private String taxCode;
    private String email;
    private String password;
    private List<SystemActor> systemActors;
    private Seniority seniority;

    public RegistrationDTO(){}

    public RegistrationDTO(String name, String lastname, LocalDate birthday, String taxCode, String email, String password, List<SystemActor> systemActors) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.taxCode = taxCode;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
    }

    public RegistrationDTO(String name, String lastname, LocalDate birthday, String taxCode, String email, String password, List<SystemActor> systemActors, Seniority seniority) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.taxCode = taxCode;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
        this.seniority = seniority;
    }

}
