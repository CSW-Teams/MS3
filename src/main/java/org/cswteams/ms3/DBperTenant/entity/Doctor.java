package org.cswteams.ms3.DBperTenant.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "doctors")
public class Doctor {

    // Getters e setters
    @Id
    @Column(name = "ms3_system_user_id")
    private Long id;

    private Date birthday;
    private String email;
    private String lastname;
    private String name;
    private String password;
    private String taxCode; // Mappato a tax_code
    private Integer seniority;

}
