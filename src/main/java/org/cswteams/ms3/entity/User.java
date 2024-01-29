package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.SystemActor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

/**
 * Entity that represents the user of the system.
 * May be a doctor or not (configurator may be another person in the hospital)
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#attori">Glossary</a>.
 * @see Doctor
 * @see SystemActor
 */
@Data
@Entity
@Table(name = "ms3_system_user")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ms3_system_user_id", nullable = false)
    protected Long id;

    @NotNull
    private String name;

    @NotNull
    private String lastname;

    /**
     * User's DOB.
     */
    @NotNull
    private LocalDate birthday;

    /**
     * User's tax code (i.e., in Italy, "Codice Fiscale")
     */
    @NotNull
    private String taxCode;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    /**
     * The "roles" that the <i>user</i> can have into the system.
     * @see SystemActor
     */
    @Enumerated
    @ElementCollection(targetClass = SystemActor.class)
    private Set<SystemActor> systemActors;


    /**
     * Create a new system <i>user</i> with the specified parameters.
     *
     * @param name         The name of the user
     * @param lastname     The surname of the user
     * @param taxCode      Italian "codice fiscale"
     * @param birthday     Date of birth
     * @param email        E-mail of the user
     * @param password     Password of the user
     * @param systemActors Set of roles of the user in the system (configurator/planner/doctor/user)
     */
    public User(String name, String lastname, String taxCode,
                LocalDate birthday, String email, String password, Set<SystemActor> systemActors) {
        this.name = name;
        this.lastname = lastname;
        this.taxCode = taxCode;
        this.birthday = birthday;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
    }

    /**
     * Default constructor needed for lombok @Data annotation on Doctor entity
     */
    protected User() {

    }

}