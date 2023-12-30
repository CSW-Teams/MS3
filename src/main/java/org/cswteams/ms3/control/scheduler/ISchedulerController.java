package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.cswteams.ms3.dto.ModifyConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;

import javax.validation.constraints.NotNull;

/**
 * Questo controller si occupa di gestire le richieste di creazione di
 * una pianificazione e della sua gestione.
 */
public interface ISchedulerController {

    /**
     * Crea una pianificazione valida per il periodo specificato e la salva nel DB.
     * @param startDate giorno di inizio della validità della pianificazione
     * @param endDate giorno di fine (compreso) della validità della pianificazione
     * @return  la pianificazione creata 
     * @throws UnableToBuildScheduleException controlla lo stack delle eccezioni per scoprire perché non è stato possibile creare la pianificazione
     */
    Schedule createSchedule(@NotNull LocalDate startDate, @NotNull LocalDate endDate);
    boolean recreateSchedule(long id) throws UnableToBuildScheduleException;
    Schedule addConcreteShift(@NotNull ConcreteShift concreteShift, boolean forced) throws IllegalScheduleException;
    Schedule modifyConcreteShift(@NotNull ModifyConcreteShiftDTO assegnazioneTurno) throws IllegalScheduleException;
    List<ScheduleDTO> readSchedules();
    List<ScheduleDTO> readIllegalSchedules();

    boolean removeSchedule(long id);
    void removeConcreteShiftFromSchedule(@NotNull ConcreteShift concreteShiftOld);
    boolean removeConcreteShift(@NotNull Long idAssegnazione);

    Schedule addConcreteShift(@NotNull RegisterConcreteShiftDTO assegnazione, boolean forced) throws AssegnazioneTurnoException, IllegalScheduleException;
}
