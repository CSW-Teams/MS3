package org.cswteams.ms3.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_id_seq")
    @SequenceGenerator(name = "schedule_id_seq", sequenceName = "schedule_id_seq")
    @NotNull
    private Long id;
    
    /** data di inizio validità della pianificazione, memorizzata come giorni da epoch */
    @NotNull
    private long startDateEpochDay;

    /** data di fine validità della pianificazione, memorizzata come giorni da epoch */
    @NotNull
    private long endDateEpochDay;

    @OneToMany(cascade = {CascadeType.ALL})
    @NotNull
    private List<AssegnazioneTurno> assegnazioniTurno;

    /** Log di messaggi corrispondenti a violazioni di vincoli.
     * Questa lista dovrebbe contenere al più un messaggio per ogni vincolo violato.
     */
    @Transient
    @OneToMany(fetch = FetchType.EAGER,cascade = {CascadeType.ALL})
    List<ViolatedConstraintLogEntry> violatedConstraintLog = new ArrayList<>();

    /** True se questa Schedule è malformata, ad esempio perché non
     * rispetta dei vincoli stringenti.
     */
    private boolean isIllegal;

    /** Se non è null, indica la causa della malformazione della pianificazione */
    private Exception causeIllegal;

    public Schedule(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
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
    public void taint(@NotNull Exception cause) {
        isIllegal = true;
        causeIllegal = cause;
    }

}
