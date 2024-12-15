package org.cswteams.ms3.dto.user;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class UserDetailsDTO {
    protected String name;
    protected String lastname;
    protected String email;
    protected LocalDate birthday;
    protected String seniority;

    protected UserDetailsDTO() {}

    public UserDetailsDTO(String name, String lastname, String email, String birthday, String seniority) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.birthday = LocalDate.parse(birthday, formatter);
        this.seniority = seniority;
    }
}