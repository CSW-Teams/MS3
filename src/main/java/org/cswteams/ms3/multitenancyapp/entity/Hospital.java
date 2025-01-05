package org.cswteams.ms3.multitenancyapp.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "ms3_hospitals")
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "address")
    private String address;

    // Costruttore con parametri
    public Hospital(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // Costruttore di default richiesto da JPA
    protected Hospital() {
    }
}
