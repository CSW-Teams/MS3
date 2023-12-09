package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import org.cswteams.ms3.entity.doctor.Doctor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AssegnazioneTurno{
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Utenti assegnati per il turno. Da non confondere con la mansione GUARDIA */
    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> utentiDiGuardia;

    /** Utenti in riserva per il turno. Questi utenti sono eligibili per L'assegnazione al turno,
     * ma non sono stati assegnati. Da non confondere con la reperibilità prevista dalla mansione GUARDIA
     */
    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> utentiReperibili;

    /**
     * Utenti rimossi dall'assegnazione turno, ad esempio per una rinuncia dell'utente stesso,
     * oppure a causa di uno scambio.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> retiredDoctors;

    @Getter
    private long dataEpochDay;

    @Getter
    @ManyToOne
    private Turno turno;

    public AssegnazioneTurno() {

    }

    public AssegnazioneTurno(LocalDate data, Turno turno, Set<Doctor> utentiReperibili, Set<Doctor> utentiDiGuardia) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.retiredDoctors = new HashSet<>();
        this.turno = turno;
    }

    public AssegnazioneTurno(LocalDate data, Turno turno) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = new HashSet<>();
        this.utentiReperibili = new HashSet<>();
        this.retiredDoctors = new HashSet<>();
        this.turno = turno;
    }

    public AssegnazioneTurno(Set<Doctor> utentiDiGuardia, Set<Doctor> utentiReperibili, long dataEpochDay, Turno turno) {
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.dataEpochDay = dataEpochDay;
        this.retiredDoctors = new HashSet<>();
        this.turno = turno;
    }

    private boolean isUserIn(Doctor u, List<Doctor> utenti){
        for (Doctor doctor : utenti) {
            if (doctor.getId().equals(u.getId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * true se l'utente è assegnato al turno tra gli allocati
     */
    public boolean isAllocated(Doctor u){

        return isUserIn(u, new ArrayList<>(utentiDiGuardia));
    }

    /**
     * true se l'utente è stato assegnato al turno in precedenza
     * ma non è più assegnato ora.
     */
    public boolean isRetired(Doctor u){
        return isUserIn(u, new ArrayList<>(retiredDoctors));
    }

    /**
     * true se l'utente è in riserva per il turno.
     * Se il turno prevede la reperibilità, l'appartenenza dell'utente 
     * alle riserve implica che esso è in reperiilità.
     */
    public boolean isReserve(Doctor u){
        return isUserIn(u, new ArrayList<>(utentiReperibili));
    }

    public Set<Doctor> getUtenti(){
        Set<Doctor> utenti = new HashSet<>();
        utenti.addAll(utentiDiGuardia);
        utenti.addAll(utentiReperibili);
        return utenti;
    }

    public LocalDate getData() {
        return LocalDate.ofEpochDay(this.dataEpochDay);
    }

    public List<Doctor> getUtentiAsList(){
        List<Doctor> utenti = new ArrayList<>();
        utenti.addAll(utentiDiGuardia);
        utenti.addAll(utentiReperibili);
        return utenti;
    }

    public void addUtentediGuardia(Doctor u) {
        this.utentiDiGuardia.add(u);
    }
    public void addUtenteReperibile(Doctor u) {
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