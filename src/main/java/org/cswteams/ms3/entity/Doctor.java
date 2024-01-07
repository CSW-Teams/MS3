package org.cswteams.ms3.entity;

import lombok.Getter;
import org.cswteams.ms3.entity.condition.*;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Doctor extends User{
    /*TODO : Check if it is necessary Setter for password on password change
     *  Should be added a Factory to set protected access to doctor (So that no one can change his credentials
     * as he pleases) */
    //@Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    //@Column(name = "doctor_id", nullable = false)
    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name="user_id")
    //private User user;

    @NotNull
    private Seniority seniority; // TODO: See if seniority is a class instead of an enum

    @ManyToMany
    private final List<PermanentCondition> permanentConditions = new ArrayList<>();
    @ManyToMany
    private final List<TemporaryCondition> temporaryConditions = new ArrayList<>();

    @ManyToMany
    private final List<Preference> preferenceList = new ArrayList<>();

    @ManyToMany
    private final List<Specialization> specializations = new ArrayList<>();


    /** Massimo monte ore pianificabile in una settimana per questo utente */
    @Transient
    private int maxWeekSchedulableHours;


    /**
     *
     * @param name
     * @param lastname
     * @param taxCode
     * @param birthday
     * @param email
     * @param password
     * @param seniority
     * @param roles
     */
    public Doctor(String name, String lastname, String taxCode,
                  LocalDate birthday, String email, String password,
                  Seniority seniority, List<SystemActor> roles) {
        super(name,lastname,taxCode,birthday,email,password,roles);
        this.maxWeekSchedulableHours = -1;
        this.seniority = seniority;

    }


    /**
     * Default constructor needed for lombok @Data annotation
     */
    protected Doctor() {
        super();
    }


    /**
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     * @param condition New condition to add for a doctor instance (Over62, ecc...)
     * @throws Exception
     */
    public void addCondition(Condition condition) throws Exception {
        if(condition.getClass().equals(PermanentCondition.class)){
            permanentConditions.add((PermanentCondition) condition);
        }else if(condition.getClass().equals(TemporaryCondition.class)){
            temporaryConditions.add((TemporaryCondition) condition);
        }else{
            throw new Exception("[ERROR] Unsupported type of condition!");
        }
    }

    /**
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     * @param preference
     */
    public void addPreference(Preference preference) {
        preferenceList.add(preference);
    }

    /**
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     * @param specialization
     */
    public void addSpecialization(Specialization specialization) {
        specializations.add(specialization);
    }

}