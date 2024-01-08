package org.cswteams.ms3.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserDetailsDTO {
    private String name;
    private String lastname;
    private String email;
    private LocalDate birthday;
    private String role;
}
