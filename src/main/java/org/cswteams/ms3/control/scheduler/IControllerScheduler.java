package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;

import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;

/**
 * Questo controller si occupa di gestire le richieste di creazione di
 * una pianificazione e della sua gestione.
 */
public interface IControllerScheduler {

    /**
     * Crea una pianificazione valida per il periodo specificato e la salva nel DB.
     * @param startDate giorno di inizio della validità della pianificazione
     * @param endDate giorno di fine (compreso) della validità della pianificazione
     * @return  la pianificazione creata 
     * @throws UnableToBuildScheduleException controlla lo stack delle eccezioni per scoprire perché non è stato possibile creare la pianificazione
     */
    Schedule createSchedule(LocalDate startDate, LocalDate endDate) throws UnableToBuildScheduleException;


}
