package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssegnazioneTurno{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Utenti assegnati per il turno. Da non confondere con la mansione GUARDIA */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Utente> utentiDiGuardia;

    /** Utenti in riserva per il turno. Questi utenti sono eligibili per L'assegnazione al turno,
     * ma non sono stati assegnati. Da non confondere con la reperibilità prevista dalla mansione GUARDIA
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Utente> utentiReperibili;

    /**
     * Utenti rimossi dall'assegnazione turno, ad esempio per una rinuncia dell'utente stesso,
     * oppure a causa di uno scambio.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Utente> retiredUsers;

    private long dataEpochDay;

    @ManyToOne
    private Turno turno;

    public AssegnazioneTurno() {

    }

    public AssegnazioneTurno(LocalDate data, Turno turno, Set<Utente> utentiReperibili, Set<Utente> utentiDiGuardia) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.turno = turno;
    }

    public AssegnazioneTurno(LocalDate data, Turno turno) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = new HashSet<>();
        this.utentiReperibili = new HashSet<>();
        this.turno = turno;
    }

    public AssegnazioneTurno(Set<Utente> utentiDiGuardia, Set<Utente> utentiReperibili, long dataEpochDay, Turno turno) {
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.dataEpochDay = dataEpochDay;
        this.turno = turno;
    }

    private boolean isUserIn(Utente u, List<Utente> utenti){
        for (Utente utente : utenti) {
            if (utente.getId().equals(u.getId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * true se l'utente è assegnato al turno tra gli allocati
     */
    public boolean isAllocated(Utente u){

        return isUserIn(u, new ArrayList<>(utentiDiGuardia));
    }

    /**
     * true se l'utente è stato assegnato al turno in precedenza
     * ma non è più assegnato ora.
     */
    public boolean isRetired(Utente u){
        return isUserIn(u, new ArrayList<>(retiredUsers));
    }

    /**
     * true se l'utente è in riserva per il turno.
     * Se il turno prevede la reperibilità, l'appartenenza dell'utente 
     * alle riserve implica che esso è in reperiilità.
     */
    public boolean isReserve(Utente u){
        return isUserIn(u, new ArrayList<>(utentiReperibili));
    }

    public LocalDate getData() {
        return LocalDate.ofEpochDay(this.dataEpochDay);
    }

    public Long getId() {
        return id;
    }

    public Set<Utente> getUtentiDiGuardia() {
        return utentiDiGuardia;
    }

    public Set<Utente> getUtentiReperibili() {
        return utentiReperibili;
    }

    public Set<Utente> getUtenti(){
        Set<Utente> utenti = new HashSet<>();
        utenti.addAll(utentiDiGuardia);
        utenti.addAll(utentiReperibili);
        return utenti;
    }

    public List<Utente> getUtentiAsList(){
        List<Utente> utenti = new ArrayList<>();
        utenti.addAll(utentiDiGuardia);
        utenti.addAll(utentiReperibili);
        return utenti;
    }

    public long getDataEpochDay() {
        return dataEpochDay;
    }

    public Turno getTurno() {
        return turno;
    }

    public void addUtentediGuardia(Utente u) {
        this.utentiDiGuardia.add(u);
    }
    public void addUtenteReperibile(Utente u) {
        this.utentiReperibili.add(u);
    }

    public AssegnazioneTurno clone(){
        return new AssegnazioneTurno(
                new HashSet<>(this.utentiDiGuardia),
                new HashSet<>(this.utentiReperibili),
                this.dataEpochDay,
                this.turno);
    }
}