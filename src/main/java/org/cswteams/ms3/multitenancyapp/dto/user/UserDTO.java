package org.cswteams.ms3.multitenancyapp.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private String email;
}
