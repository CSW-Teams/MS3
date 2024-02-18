package org.cswteams.ms3.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserCreationDTO {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String taxCode;
    private String email;
    private String password;
    private List<String> systemActors;
    //added by Fanfa
    private String seniority;
}
