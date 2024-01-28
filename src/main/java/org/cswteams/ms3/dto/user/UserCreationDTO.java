package org.cswteams.ms3.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserCreationDTO {

    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String lastname;
    @NotNull
    private LocalDate birthday;
    @NotNull
    @NotEmpty
    private String taxCode;
    @NotNull
    @Email
    private String email;
    @NotNull
    private String password;
    @NotNull
    @NotEmpty
    private List<@NotNull String> systemActors;
    //added by Fanfa
    @NotNull
    @NotEmpty
    private String seniority;
}
