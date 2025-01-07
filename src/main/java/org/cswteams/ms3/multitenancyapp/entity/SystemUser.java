package org.cswteams.ms3.multitenancyapp.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "ms3_system_users")
public class SystemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "lastname")
    private String lastname;

    @NotNull
    @Column(name = "birthday")
    private LocalDate birthday;

    @NotNull
    @Column(name = "tax_code")
    private String taxCode;

    @Email
    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @NotNull
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "tenant")
    private String tenant;

    // Costruttore con parametri
    public SystemUser(String name, String lastname, String taxCode, LocalDate birthday,
                      String email, String password, String tenant) {
        this.name = name;
        this.lastname = lastname;
        this.birthday = birthday;
        this.taxCode = taxCode;
        this.email = email;
        this.password = password;
        this.tenant = tenant;
    }

    // Costruttore di default richiesto da JPA
    protected SystemUser() {
    }
}
