package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.SystemActor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "ms3_system_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
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
    private SystemActor systemActor;


    /**
     * Entity that represents the user of the system.
     * May be a doctor or not (configurator may be another person in the hospital)
     * @param name The name of the user
     * @param lastname The surname of the user
     * @param taxCode Italian "codice fiscale"
     * @param birthday Date of birth
     * @param email E-mail of the user
     * @param password Password of the user
     * @param systemActor Role of the user in the system (configurator/planner/doctor/user)
     */
    public User(String name, String lastname, String taxCode,
                LocalDate birthday, String email, String password, SystemActor systemActor){
        this.name = name;
        this.lastname = lastname;
        this.taxCode = taxCode;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
        this.systemActor = systemActor;
    }

    /**
     * Default constructor needed for lombok @Data annotation on Doctor entity
     */
    protected User(){

    }

}
