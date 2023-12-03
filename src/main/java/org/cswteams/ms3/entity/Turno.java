package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
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
    private List<UserCategoryPolicy> categoryPolicies;

    public Turno(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, List<RuoloNumero> ruoliNumero, boolean reperibilitaAttiva) throws  TurnoException {

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

    public Turno(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, List<RuoloNumero> ruoliNumero) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, ruoliNumero, false);
    }

    public Turno(LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, boolean reperibilitaAttiva) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, new ArrayList<>(Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1),new RuoloNumero(RuoloEnum.STRUTTURATO,1))), reperibilitaAttiva);
    }

    public Turno(long id,LocalTime oraInizio, Duration durata, Servizio servizio, MansioneEnum mansione, TipologiaTurno tipologia, boolean reperibilitaAttiva) throws  TurnoException {
        this(oraInizio, durata, servizio, mansione, tipologia, new ArrayList<>(Arrays.asList(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1),new RuoloNumero(RuoloEnum.STRUTTURATO,1))),reperibilitaAttiva);
        this.id = id;
    }

    public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, Duration durata,
            GiorniDellaSettimanaBitMask giorniDiValidità, Servizio servizio, MansioneEnum mansione, boolean reperibilitaAttiva) throws  TurnoException {
        this(id, oraInizio, durata, servizio, mansione, tipologiaTurno, reperibilitaAttiva);
        this.giorniDiValidità = giorniDiValidità;
    }

    public Turno() {
        this.ruoliNumero = new ArrayList<>();
        ruoliNumero.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));
        ruoliNumero.add(new RuoloNumero(RuoloEnum.STRUTTURATO,1));
    }

    //Restituisce il numero di minuto di lavoro per questo turno
    public long getMinutidiLavoro(){
        return durata.toMinutes();
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
