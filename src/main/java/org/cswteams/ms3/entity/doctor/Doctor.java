package org.cswteams.ms3.entity.doctor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.category.*;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User{
    /*TODO : Check if it is necessary Setter for password on password change
    *  Should be added a Factory to set protected access to doctor (So that no one can change his credentials
    * as he pleases) */

    @NotNull
    private AttoreEnum attore;

    @OneToMany
    private List<Desiderata> desiderataList;

    @ManyToMany
    private List<PermanentCondition> permanentConditions;
    @ManyToMany
    private List<TemporaryCondition> temporaryConditions;

    @ManyToMany
    private  List<Specialization> specializations;

    @ManyToMany
    private  List<Structure> structures;

    /** Massimo monte ore pianificabile in una settimana per questo utente */
    private int maxWeekSchedulableHours;


    public Doctor(String name, String lastname, String taxCode,
                  LocalDate birthday, String email, String password,
                  RuoloEnum role, AttoreEnum attore) {
        super(name,lastname,taxCode,birthday,email,password,role);
        this.maxWeekSchedulableHours = -1;
        this.permanentConditions = new ArrayList<>();
        this.temporaryConditions = new ArrayList<>();
        this.specializations = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.desiderataList = new ArrayList<>();
        this.attore=attore;
    }

    public Doctor(Long id, String name, String lastname, String taxCode,
                  LocalDate birthday, String email, String password,
                  RuoloEnum role, AttoreEnum attore) {

        this(name, lastname, taxCode, birthday, email, password, role, attore);
        this.id = id;
    }

    public Doctor(Long id, String name, String lastname, LocalDate birthday,
                  String taxCode, RuoloEnum role, String email,
                  String password, int maxWeekSchedulableHours, AttoreEnum attore) {

        this(id,name, lastname, taxCode, birthday, email, password, role, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
    }

     public Doctor(Long id, String name, String lastname, LocalDate birthday,
                   String taxCode, RuoloEnum role, String email,
                   String password, int maxWeekSchedulableHours, List<PermanentCondition> permanentConditions, AttoreEnum attore) {

        this(id,name, lastname, taxCode, birthday, email, password, role, attore);
        this.maxWeekSchedulableHours = maxWeekSchedulableHours;
        this.permanentConditions = permanentConditions;
    }


    public Doctor(Long id, String name, String lastname, String taxCode,
                  LocalDate birthday, String email, String password,
                  RuoloEnum role, List<PermanentCondition> permanentConditions, AttoreEnum attore) {
        this(id,name, lastname, taxCode, birthday, email, password, role, attore);
        this.permanentConditions = permanentConditions;
    }

    public Doctor(Long id, String nome, String lastname, String taxCode, LocalDate birthday, String email, RuoloEnum role, List<PermanentCondition> permanentConditions,
                  String password, List<Desiderata> desiderataList, AttoreEnum attore) {
        this(id,nome, lastname, taxCode, birthday, email, password, role, attore);
        this.permanentConditions = permanentConditions;
        this.desiderataList = desiderataList;
    }


}
