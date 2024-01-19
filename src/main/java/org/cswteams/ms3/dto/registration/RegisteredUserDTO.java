package org.cswteams.ms3.dto.registration;

import lombok.Getter;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;

import java.time.LocalDate;
import java.util.Set;

/**
 * DTO used in the Registration use case (from Service to REST Controller)
 */
@Getter
public class RegisteredUserDTO {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String taxCode;
    private String email;
    private String password;
    private Set<SystemActor> systemActors;
    private Seniority seniority;


    public RegisteredUserDTO(Long id, String name, String lastname, LocalDate birthday, String taxCode, String email, String password, Set<SystemActor> systemActors) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.taxCode = taxCode;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
    }

    public RegisteredUserDTO(Long id, String name, String lastname, LocalDate birthday, String taxCode, String email, String password, Set<SystemActor> systemActors, Seniority seniority) {
        this.id = id;
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
