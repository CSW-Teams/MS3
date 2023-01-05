package org.cswteams.ms3.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;

import lombok.Data;

/** Rappresenta una pianificazione dei turni in un intervallo di date */
@Entity
@Data
public class Schedule {

    /** data di inizio validità della pianificazione, memorizzata come giorni da epoch */
    private long startDateEpochDay;

    /** data di fine validità della pianificazione, memorizzata come giorni da epoch */
    private long endDateEpochDay;

    /** Assegnazioni dei turni previste dalla pianificazione */
    private List<AssegnazioneTurno> assegnazioniTurno;

    public Schedule(LocalDate startDate, LocalDate endDate) {
        this.startDateEpochDay = startDate.toEpochDay();
        this.endDateEpochDay = endDate.toEpochDay();
    }

    public LocalDate getStartDate() {
        return LocalDate.ofEpochDay(startDateEpochDay);
    }

    public LocalDate getEndDate() {
        return LocalDate.ofEpochDay(endDateEpochDay);
    }
    
}
