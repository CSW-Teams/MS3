package org.cswteams.ms3.entity.doctor;

import lombok.Data;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.category.Condition;
import org.cswteams.ms3.entity.category.Rotation;
import org.cswteams.ms3.entity.category.Specialization;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doctor {
    /*TODO : Check if it is necessary Setter for password on password change*/
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctor_id_seq")
    @SequenceGenerator(name = "doctor_id_seq", sequenceName = "doctor_id_seq")
    @NotNull
    private Long id;
    
    @NotNull
    private String name;

    @NotNull
    private String lastname;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String taxCode; // Codice Fiscale

    @NotNull
    private RuoloEnum ruoloEnum;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private AttoreEnum attore;



    @OneToMany
    private List<Desiderata> desiderataList;

    @ManyToMany
    private List<Condition> conditions;

    @ManyToMany
    private  List<Specialization> specializations;

    @ManyToMany
    private  List<Rotation> rotations;

    /** Massimo monte ore pianificabile in una settimana per questo utente */
    private int maxWeekSchedulableHours;

    protected Doctor() {

    }

    public Doctor(String name, String lastname, String taxCode,
                  LocalDate birthDate, String email, String password,
                  RuoloEnum ruoloEnum, AttoreEnum attore) {
        this.name = name;
        this.lastname = lastname;
        this.taxCode = taxCode;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.ruoloEnum = ruoloEnum;
        this.maxWeekSchedulableHours = -1;
        this.conditions = new ArrayList<>();
        this.specializations = new ArrayList<>();
        this.rotations = new ArrayList<>();
        this.desiderataList = new ArrayList<>();
        this.attore=attore;
    }

    public Doctor(Long id, String name, String lastname, String taxCode,
                  LocalDate birthDate, String email, String password,
                  RuoloEnum ruoloEnum, AttoreEnum attore) {

        this(name, lastname, taxCode, birthDate, email, password, ruoloEnum, attore);
        this.id = id;
    }

    public Doctor(Long id, String name, String lastname, LocalDate birthDate,
                  String taxCode, RuoloEnum ruoloEnum, String email,
                  String password, int maxWeekSchedulableHours, AttoreEnum attore) {

        this(id,name, lastname, taxCode, birthDate, email, password, ruoloEnum, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
    }

     public Doctor(Long id, String name, String lastname, LocalDate birthDate,
                   String taxCode, RuoloEnum ruoloEnum, String email,
                   String password, int maxWeekSchedulableHours, List<Condition> conditions, AttoreEnum attore) {

        this(id,name, lastname, taxCode, birthDate, email, password, ruoloEnum, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
        this.conditions = conditions;
    }


    public Doctor(Long id, String name, String lastname, String taxCode,
                  LocalDate birthDate, String email, String password,
                  RuoloEnum ruoloEnum, List<Condition> conditions, AttoreEnum attore) {
        this(id,name, lastname, taxCode, birthDate, email, password, ruoloEnum, attore);
        this.conditions = conditions;
    }

    public Doctor(Long id, String nome, String lastname, String taxCode, LocalDate birthDate, String email, RuoloEnum ruoloEnum, List<Condition> conditions,
                  String password, List<Desiderata> desiderataList, AttoreEnum attore) {
        this(id,nome, lastname, taxCode, birthDate, email, password, ruoloEnum, attore);
        this.conditions = conditions;
        this.desiderataList = desiderataList;
    }


}
