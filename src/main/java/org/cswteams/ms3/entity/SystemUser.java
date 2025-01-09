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
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ms3_system_user_id", nullable = false)
    protected Long id;

    @NotNull
    private String name;

    @NotNull
    private String lastname;

    /**
     * SystemUser's DOB.
     */
    @NotNull
    private LocalDate birthday;

    /**
     * SystemUser's tax code (i.e., in Italy, "Codice Fiscale")
     */
    @NotNull
    @Column(name = "tax_code")
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
    @ElementCollection(targetClass = SystemActor.class, fetch = FetchType.EAGER)
    private Set<SystemActor> systemActors;

    @NotNull
    private String tenant;

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
     * @param tenant       Tenant of the user
     */
    // Costruttore con parametri
    public SystemUser(String name, String lastname, String taxCode, LocalDate birthday,
                      String email, String password, Set<SystemActor> systemActors, String tenant) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.taxCode = taxCode;
        this.email = email;
        this.password = password;
        this.systemActors = systemActors;
        this.tenant = tenant;
    }

    // Costruttore di default richiesto da JPA
    protected SystemUser() {
    }
}