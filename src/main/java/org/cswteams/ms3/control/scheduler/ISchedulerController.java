package org.cswteams.ms3.control.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.cswteams.ms3.dto.ModifyConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.showscheduletoplanner.ShowScheduleToPlannerDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;

/**
 * This controller is responsible for handling creation requests
 * planning and its management.
 */
public interface ISchedulerController {

    Set<ShowScheduleToPlannerDTO> getAllSchedulesWithDates();

    /**
     * Creates a schedule valid for the specified period and saves it in the DB.
     *
     * @param startDate starting day of the schedule validity
     * @param endDate   end day (inclusive) of the validity of the schedule
     * @return the created schedule
     */
    Schedule createSchedule(LocalDate startDate, LocalDate endDate, List<DoctorUffaPriority> doctorUffaPriorityList);

    /**
     * Proxy of the method createSchedule, when the DoctorUffaPriority list cannot be retrieved.
     * This method creates a new shift schedule by specifying start date and end date.
     *
     * @param startDate First date of the shift schedule
     * @param endDate   Last date of the shift schedule
     * @return An instance of schedule if correctly created and saved in persistence, null otherwise
     */
    Schedule createSchedule(LocalDate startDate, LocalDate endDate);

    /**
     * This method recreates an existing shift scheduling. It is not possible to recreate a schedule in the past.
     *
     * @param id An existing schedule ID
     * @return Boolean that represents if the regeneration ended successfully
     */
    boolean recreateSchedule(long id) throws UnableToBuildScheduleException;

    /**
     * This method adds a new concrete shift to an existing schedule. In particular, it looks for the schedule containing
     * the date of the new concrete shift and passes it to ScheduleBuilder.
     *
     * @param concreteShift The new concrete shift to be added to the schedule
     * @param forced        If true, the concrete shift will be added if it respects all the non-violable constraints;
     *                      if false, the concrete shift will be added only if it respects all the existing constraints.
     * @return An instance of the updated shift schedule
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
    Schedule addConcreteShift(ConcreteShift concreteShift, boolean forced) throws IllegalScheduleException;

    /**
     * This method modifies an existing concrete shift. In particular, it removes the existing concrete shifts, checks if
     * the new version of the concrete shift respects all the constraints and, if the checks succeed, then saves the new
     * version of the concrete shift into the database. Instead, if there are some violated constraints, then the old
     * version of the concrete shift is saved into the database again.
     *
     * @param modifyConcreteShiftDTO DTO instance representing the changes to make in an existing concrete shift
     * @return An instance of the updated shift schedule containing the modified concrete shift
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
    Schedule modifyConcreteShift(ModifyConcreteShiftDTO modifyConcreteShiftDTO) throws IllegalScheduleException;

    /**
     * This method retrieves all the existing schedules from the database.
     *
     * @return List of DTO instances representing all the existing schedules to be delivered to the frontend
     */
    List<ScheduleDTO> readSchedules();

    /**
     * This method retrieves the illegal schedules from the database.
     *
     * @return List of DTO instances representing the illegal schedules to be delivered to the frontend
     */
    List<ScheduleDTO> readIllegalSchedules();

    /**
     * This method removes an existing shift schedule. This operation can be performed correctly only if the schedule is
     * in the future and not in the past.
     *
     * @param id ID of the shift schedule to be removed
     * @return Boolean that represents if the deletion was successful
     */
    boolean removeSchedule(long id);

    /**
     * This method removes a concrete shift from a schedule but not from the database.
     *
     * @param concreteShiftOld Concrete shift to be removed
     */
    void removeConcreteShiftFromSchedule(ConcreteShift concreteShiftOld);

    /**
     * This method removes a concrete shift from the database.
     *
     * @param concreteShiftId ID of the concrete shift to be removed
     * @return Boolean that represents if the deletion was successful
     */
    boolean removeConcreteShift(Long concreteShiftId);

    /**
     * This method adds a new concrete shift to an existing schedule; the concrete shift is described by the DTO parameter.
     *
     * @param registerConcreteShiftDTO DTO class that describes the new concrete shift
     * @param forced                   If true, the concrete shift will be added if it respects all the non-violable constraints;
     *                                 if false, the concrete shift will be added only if it respects all the existing constraints.
     * @return An instance of the updated shift schedule
     * @throws ConcreteShiftException   Rised if the DTO parameter describes a non-existing concrete shift
     * @throws IllegalScheduleException Rised if the new concrete shift makes the schedule illegal
     */
    Schedule addConcreteShift(RegisterConcreteShiftDTO registerConcreteShiftDTO, boolean forced) throws ConcreteShiftException, IllegalScheduleException;
}
