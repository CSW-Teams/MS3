package org.cswteams.ms3.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.category.Condition;
import org.cswteams.ms3.entity.category.PermanentCondition;
import org.cswteams.ms3.entity.category.Rotation;
import org.cswteams.ms3.entity.category.Specialization;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
import org.cswteams.ms3.entity.policy.RotationPolicy;
import org.cswteams.ms3.entity.policy.SpecializationPolicy;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shift_id", nullable = false)
    private Long id;

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private Duration durata;

    private boolean reperibilitaAttiva;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<RuoloNumero> ruoliNumero ;

    /** Quale mansione è da svolgere per questo turno */
    private MansioneEnum mansione;

    /**
     * In quali giorni della settimana questo turno può essere assegnato
     */
    @Embedded
    private GiorniDellaSettimanaBitMask giorniDiValidità;

    @ManyToOne
    private Servizio servizio;

    /**
     * Le categorie utenti richieste o vietate per questo turno
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<ConditionPolicy> conditionPolicies;

    /**
     * Le categorie utenti richieste o vietate per questo turno
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<RotationPolicy> rotationPolicies;

    /**
     * Le categorie utenti richieste o vietate per questo turno
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<SpecializationPolicy> specializationPolicies;

    public Shift(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, List<RuoloNumero> ruoliNumero, boolean reperibilitaAttiva) throws  TurnoException {

        //Controllo che la mansione inserita è presente per il servizio inserito
        boolean check = false;
        for(MansioneEnum mansioneServizio: servizio.getMansioni()){
            if (mansioneServizio.equals(mansione)) {
                check = true;
                break;
            }
        }
        if(!check) throw new TurnoException("Mansione inserita non compatibile con il servizio inserito");

        this.oraInizio = oraInizio;
        this.durata = durata;
        this.servizio = servizio;
        this.mansione = mansione;
        this.tipologiaTurno = tipologia;
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

        this.ruoliNumero = ruoliNumero;
        this.reperibilitaAttiva = reperibilitaAttiva;
    }

    public Shift(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, List<RuoloNumero> ruoliNumero) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, ruoliNumero, false);
    }

    public Shift(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, boolean reperibilitaAttiva) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, new ArrayList<>(Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1),new RuoloNumero(RuoloEnum.STRUTTURATO,1))), reperibilitaAttiva);
    }

    public Shift(long id, LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, boolean reperibilitaAttiva) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, new ArrayList<>(Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1),new RuoloNumero(RuoloEnum.STRUTTURATO,1))),reperibilitaAttiva);
        this.id = id;
    }

    public Shift(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, Duration durata,
                 GiorniDellaSettimanaBitMask giorniDiValidità, Servizio servizio, MansioneEnum mansione, boolean reperibilitaAttiva) throws  TurnoException {
        this(id, oraInizio, durata, servizio, mansione, tipologiaTurno, reperibilitaAttiva);
        this.giorniDiValidità = giorniDiValidità;
    }

    public Shift() {
        this.ruoliNumero = new ArrayList<>();
        ruoliNumero.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));
        ruoliNumero.add(new RuoloNumero(RuoloEnum.STRUTTURATO,1));
    }

    //Restituisce il numero di minuto di lavoro per questo turno
    public long getMinutidiLavoro(){
        return durata.toMinutes();
    }

    public void setBannedConditions(Set<Condition> categorieVietate){
        List<ConditionPolicy> policies = new ArrayList<>();
        for (Condition cu : categorieVietate) {
            policies.add(new ConditionPolicy((PermanentCondition) cu, this, UserCategoryPolicyValue.EXCLUDE));
        }
        this.setConditionPolicies(policies);
    }

    public void setBannedRotations(Set<Rotation> categorieVietate){
        List<RotationPolicy> policies = new ArrayList<>();
        for (org.cswteams.ms3.entity.category.Rotation cu : categorieVietate) {
            policies.add(new RotationPolicy(cu, this, UserCategoryPolicyValue.EXCLUDE));
        }
        this.setRotationPolicies(policies);
    }

    public void setBannedSpecialization(Set<Specialization> categorieVietate){
        List<SpecializationPolicy> policies = new ArrayList<>();
        for (Specialization cu : categorieVietate) {
            policies.add(new SpecializationPolicy(cu, this, UserCategoryPolicyValue.EXCLUDE));
        }
        this.setSpecializationPolicies(policies);
    }

    public Set<Condition> getBannedConditions(){
        Set<Condition> bannedCategories = new HashSet<>();
        for (ConditionPolicy p : this.getConditionPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedCategories.add(p.getPermanentCondition());
            }
        }
        return bannedCategories;
    }

    public Set<Rotation> getBannedRotations(){
        Set<Rotation> bannedRotation = new HashSet<>();
        for (RotationPolicy p : this.getRotationPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedRotation.add(p.getRotation());
            }
        }
        return bannedRotation;
    }

    public Set<Specialization> getBannedSpecializations(){
        Set<Specialization> bannedSpecializations = new HashSet<>();
        for (SpecializationPolicy p : this.getSpecializationPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                bannedSpecializations.add(p.getSpecialization());
            }
        }
        return bannedSpecializations;
    }

    /**
     * Calcola il numero di utenti necessari per il turno sommando
     * il numero di utenti richiesto per ogni ruolo.
     * @return numero di utenti necessari per il turno.
     */
    public int getNumRequiredUsers(){

        int numUtenti = 0;
        for(RuoloNumero ruoloNumero: ruoliNumero){
            numUtenti += ruoloNumero.getNumero();
        }
        return numUtenti;
    }

}
