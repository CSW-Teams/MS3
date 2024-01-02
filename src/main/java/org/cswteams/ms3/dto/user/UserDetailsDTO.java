package org.cswteams.ms3.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDetailsDTO {
    private String name;
    private String lastname;
    private String email;
    private LocalDate birthday;
    private String role;
}
