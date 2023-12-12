package org.cswteams.ms3.entity.doctor;

import lombok.Data;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.management.relation.Role;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    @NotNull
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String lastname;

    @NotNull
    private LocalDate birthday;

    @NotNull
    private String taxCode; // Codice Fiscale

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private RuoloEnum role;

    public User(String name, String lastname, String taxCode,
                LocalDate birthday, String email, String password, Role role){
        this.name = name;
        this.lastname = lastname;
        this.taxCode = taxCode;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
