package org.cswteams.ms3.entity;

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
public class ConcreteShift {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Utenti assegnati per il shift. Da non confondere con la mansione GUARDIA */
    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> utentiDiGuardia;

    /** Utenti in riserva per il shift. Questi utenti sono eligibili per L'assegnazione al shift,
     * ma non sono stati assegnati. Da non confondere con la reperibilità prevista dalla mansione GUARDIA
     */
    @Getter
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> utentiReperibili;

    /**
     * Utenti rimossi dall'assegnazione shift, ad esempio per una rinuncia dell'utente stesso,
     * oppure a causa di uno scambio.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Doctor> retiredDoctors;

    @Getter
    private long dataEpochDay;

    @Getter
    @ManyToOne
    private Shift shift;

    public ConcreteShift() {

    }

    public ConcreteShift(LocalDate data, Shift shift, Set<Doctor> utentiReperibili, Set<Doctor> utentiDiGuardia) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.retiredDoctors = new HashSet<>();
        this.shift = shift;
    }

    public ConcreteShift(LocalDate data, Shift shift) {
        this.dataEpochDay = data.toEpochDay();
        this.utentiDiGuardia = new HashSet<>();
        this.utentiReperibili = new HashSet<>();
        this.retiredDoctors = new HashSet<>();
        this.shift = shift;
    }

    public ConcreteShift(Set<Doctor> utentiDiGuardia, Set<Doctor> utentiReperibili, long dataEpochDay, Shift shift) {
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.dataEpochDay = dataEpochDay;
        this.retiredDoctors = new HashSet<>();
        this.shift = shift;
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
     * true se l'utente è assegnato al shift tra gli allocati
     */
    public boolean isAllocated(Doctor u){

        return isUserIn(u, new ArrayList<>(utentiDiGuardia));
    }

    /**
     * true se l'utente è stato assegnato al shift in precedenza
     * ma non è più assegnato ora.
     */
    public boolean isRetired(Doctor u){
        return isUserIn(u, new ArrayList<>(retiredDoctors));
    }

    /**
     * true se l'utente è in riserva per il shift.
     * Se il shift prevede la reperibilità, l'appartenenza dell'utente
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

    public ConcreteShift clone(){
        return new ConcreteShift(
                new HashSet<>(this.utentiDiGuardia),
                new HashSet<>(this.utentiReperibili),
                this.dataEpochDay,
                this.shift);
    }
}