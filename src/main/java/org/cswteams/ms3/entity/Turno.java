package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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

    /**
     * In quali giorni della settimana questo turno può essere assegnato
     */
    @Embedded
    private GiorniDellaSettimanaBitMask giorniDiValidità;

    @ManyToOne
    private Servizio servizio;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<CategoriaUtentiEnum> categorieVietate;

    public Turno(LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, Set<CategoriaUtentiEnum> categorieVietate, boolean giornoSuccessivo) throws  TurnoException {

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
        this.categorieVietate = categorieVietate;
        this.giorniDiValidità = (new GiorniDellaSettimanaBitMask()).enableAllDays();

    }

    public Turno(long id,LocalTime oraInizio, LocalTime oraFine, Servizio servizio, TipologiaTurno tipologia, Set<CategoriaUtentiEnum> categorieVietate, boolean giornoSuccessivo) throws  TurnoException {
        this(oraInizio, oraFine, servizio, tipologia, categorieVietate,giornoSuccessivo);
        this.id = id;
    }

    public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Set<CategoriaUtentiEnum> categorieVietate, Servizio servizio,boolean giornoSuccessivo) throws  TurnoException {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno, categorieVietate,giornoSuccessivo);
        this.giorniDiValidità = giorniDiValidità;
    }

     public Turno(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            GiorniDellaSettimanaBitMask giorniDiValidità, Set<CategoriaUtentiEnum> categorieVietate, Servizio servizio, int numUtentiGuardia, int numUtentiReperibilita, boolean giornoSuccessivo) throws TurnoException {
        this(id, oraInizio, oraFine, servizio, tipologiaTurno, categorieVietate, giornoSuccessivo);
        this.giorniDiValidità = giorniDiValidità;
        this.numUtentiGuardia = numUtentiGuardia;
        this.numUtentiReperibilita = numUtentiReperibilita;
    }

    public Turno() {
    }

    public Turno(LocalTime oi, LocalTime of, Servizio servizio, TipologiaTurno tt, Set<CategoriaUtentiEnum> es) {
        this.oraFine=oi;
        this.oraInizio=of;
        this.tipologiaTurno=tt;
        this.categorieVietate=es;
        this.servizio=servizio;
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

}
