package org.cswteams.ms3.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String lastname;
    private LocalDate birthday;
    private List<String> systemActors;
}
