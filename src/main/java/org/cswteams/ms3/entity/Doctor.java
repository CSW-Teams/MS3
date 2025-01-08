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
import java.util.Set;

/**
 * A <i>Doctor</i> in the system.
 *
 * @see <a href="https://github.com/CSW-Teams/MS3/wiki#medici">Glossary</a>.
 * @see TenantUser
 * @see SystemActor
 */
@Getter
@Entity
public class Doctor extends TenantUser {
    /*TODO : Check if it is necessary Setter for password on password change
     *  Should be added a Factory to set protected access to doctor (So that no one can change his credentials
     * as he pleases) */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Long id;

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


    /**
     * Maximum number of hours that can be planned in a week for this <i>Doctor</i>.
     */
    @Transient
    private int maxWeekSchedulableHours;


    /**
     * Create a new <i>Doctor</i> with the specified parameters.
     *
     * @param name      The name of the doctor
     * @param lastname  The surname of the doctor
     * @param taxCode   Italian "codice fiscale"
     * @param birthday  Date of birth
     * @param email     E-mail of the doctor
     * @param password  Password of the doctor
     * @param seniority Seniority of the doctor
     * @param roles     Set of roles of the doctor in the system (configurator/planner/doctor/user)
     */
    public Doctor(String name, String lastname, String taxCode,
                  LocalDate birthday, String email, String password,
                  Seniority seniority, Set<SystemActor> roles) {
        super(name, lastname, taxCode, birthday, email, password, roles);
        this.maxWeekSchedulableHours = -1;
        this.seniority = seniority;

    }


    /**
     * Default constructor needed by Lombok
     */
    protected Doctor() {
        super();
    }


    /**
     * Add a <i>condition</i> to this <i>Doctor</i>.
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     *
     * @param condition New condition to add for a doctor instance (Over62, ecc...)
     * @throws Exception if the type of the <i>condition</i> is not supported
     */
    public void addCondition(Condition condition) throws Exception {
        if (condition.getClass().equals(PermanentCondition.class)) {
            permanentConditions.add((PermanentCondition) condition);
        } else if (condition.getClass().equals(TemporaryCondition.class)) {
            temporaryConditions.add((TemporaryCondition) condition);
        } else {
            throw new Exception("[ERROR] Unsupported type of condition!");
        }
    }

    /**
     * Add a scheduling <i>preference</i> to this <i>Doctor</i>.
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     *
     * @param preference scheduling preference for the <i>Doctor</i>
     */
    public void addPreference(Preference preference) {
        preferenceList.add(preference);
    }

    /**
     * Add a <i>specialization</i> to this <i>Doctor</i>.
     * TODO: Create design pattern to handle adding new parameters to doctor (Factory/Builder/Decorator)
     * TODO: Define custom exception to recognize this specific case
     *
     * @param specialization <i>Doctor</i>'s <i>specialization</i>.
     */
    public void addSpecialization(Specialization specialization) {
        specializations.add(specialization);
    }

}