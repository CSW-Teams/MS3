package org.cswteams.ms3.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import lombok.Data;

/** Rappresenta una pianificazione dei turni assegnati in un intervallo di date */
@Entity
@Data
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "startDateEpochDay",
        "endDateEpochDay"
    })
})
public class Schedule {

    @Id
    @GeneratedValue
    private Long id;
    
    /** data di inizio validità della pianificazione, memorizzata come giorni da epoch */
    private long startDateEpochDay;

    /** data di fine validità della pianificazione, memorizzata come giorni da epoch */
    private long endDateEpochDay;

    /** Assegnazioni dei turni previste dalla pianificazione */
    @OneToMany(cascade = {CascadeType.ALL})
    private List<AssegnazioneTurno> assegnazioniTurno;

    /** Log di messaggi corrispondenti a violazioni di vincoli.
     * Questa lista dovrebbe contenere al più un messaggio per ogni vincolo violato.
     */
    @OneToMany(cascade = {CascadeType.ALL})
    List<ViolatedConstraintLogEntry> violatedConstraintLog = new ArrayList<>();

    /** True se questa Schedule è malformata, ad esempio perché non
     * rispetta dei vincoli stringenti.
     */
    private boolean isIllegal;

    /** Se non è null, indica la causa della malformazione della pianificazione */
    private Exception causeIllegal;

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDateEpochDay = startDate.toEpochDay();
        this.endDateEpochDay = endDate.toEpochDay();
    }

    public Schedule(){

    }

    public LocalDate getStartDate() {
        return LocalDate.ofEpochDay(startDateEpochDay);
    }

    public LocalDate getEndDate() {
        return LocalDate.ofEpochDay(endDateEpochDay);
    }

    /** resets illegal flag and clears illegalCause and violations log */
    public void purify() {
        violatedConstraintLog.clear();
        redeem();
    }

     /** resets illegal flag and clears illegalCause*/
     public void redeem(){
        isIllegal = false;
        causeIllegal = null;
     }

    /** rende illegale la pianificazione specificando una causa */
    public void taint(Exception cause) {
        isIllegal = true;
        causeIllegal = cause;
    }
}
