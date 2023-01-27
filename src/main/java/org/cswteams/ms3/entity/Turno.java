package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private TipologiaTurno tipologiaTurno;

    private LocalTime oraInizio;

    private LocalTime oraFine;

    //Utile nel caso di turno notturno. Questa variabile sarà vera se un turno inizia un giorno e termina il successivo.
    private boolean giornoSuccessivo;
    
    private int numUtentiReperibilita;
    private int numUtentiGuardia;

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
    private List<UserCategoryPolicy> categoryPolicies;

    public Turno(LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, boolean giornoSuccessivo) throws  TurnoException {

        // Se l'ora di inizio segue l'ora di fine verrà sollevata eccezzione solo se il turno non è configurato
        // per iniziare in un giorno e finire in quello seguente
        if(oraInizio.isAfter(oraFine) && !giornoSuccessivo){
            throw new TurnoException("Data di inizio non può seguire data di fine");
        }

        this.giornoSuccessivo=giornoSuccessivo;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        this.servizio = servizio;
        this.tipologiaTurno = tipologia;
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

        this.ruoliNumero = new ArrayList<>();
        ruoliNumero.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));
        ruoliNumero.add(new RuoloNumero(RuoloEnum.STRUTTURATO,1));

    }

    public Turno(long id,LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, boolean giornoSuccessivo) throws  TurnoException {
        this(oraInizio, oraFine, servizio, tipologia,giornoSuccessivo);
        this.id = id;
    }

    public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Servizio servizio,boolean giornoSuccessivo) throws  TurnoException {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno, giornoSuccessivo);
        this.giorniDiValidità = giorniDiValidità;
    }

     public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Servizio servizio, int numUtentiGuardia, int numUtentiReperibilita, boolean giornoSuccessivo) throws TurnoException {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno, giornoSuccessivo);
        this.giorniDiValidità = giorniDiValidità;
        this.numUtentiGuardia = numUtentiGuardia;
        this.numUtentiReperibilita = numUtentiReperibilita;
    }

    public Turno() {
        this.ruoliNumero = new ArrayList<>();
        ruoliNumero.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));
        ruoliNumero.add(new RuoloNumero(RuoloEnum.STRUTTURATO,1));
    }

    public Turno(LocalTime oi, LocalTime of, Servizio servizio, TipologiaTurno tt) {
        this.oraFine=oi;
        this.oraInizio=of;
        this.tipologiaTurno=tt;
        this.servizio=servizio;
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

        this.ruoliNumero = new ArrayList<>();
        ruoliNumero.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));
        ruoliNumero.add(new RuoloNumero(RuoloEnum.STRUTTURATO,1));
    }


    public void setGiornoSuccessivo(){
        this.giornoSuccessivo = true;
    }

    //Restituisce il numero di minuto di lavoro per quetso turno
    public long getMinutidiLavoro(){
        LocalDateTime inizio = LocalDateTime.of(LocalDate.now(),this.oraInizio);
        LocalDateTime fine;
        if(this.giornoSuccessivo)
            fine = LocalDateTime.of(LocalDate.now().plusDays(1),this.oraFine);
         else
            fine = LocalDateTime.of(LocalDate.now(),this.oraFine);
        return inizio.until(fine, ChronoUnit.MINUTES);
    }

    public void setCategorieVietate(Set<Categoria> categorieVietate){
        List<UserCategoryPolicy> policies = new ArrayList<>();
        for (Categoria cu : categorieVietate) {
            policies.add(new UserCategoryPolicy(cu, this, UserCategoryPolicyValue.EXCLUDE));
        }
        this.setCategoryPolicies(policies);
    }

    public Set<Categoria> getCategorieVietate(){
        Set<Categoria> categorieVietate = new HashSet<>();
        for (UserCategoryPolicy p : this.getCategoryPolicies()) {
            if (p.getPolicy().equals(UserCategoryPolicyValue.EXCLUDE)) {
                categorieVietate.add(p.getCategoria());
            }
        }
        return categorieVietate;
    }

}
