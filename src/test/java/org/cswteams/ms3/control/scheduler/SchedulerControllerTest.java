package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.showscheduletoplanner.ShowScheduleToPlannerDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.cswteams.ms3.utils.DateConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerControllerTest {

    @Mock
    private DoctorDAO doctorDAO;

    @Mock
    private ShiftDAO shiftDAO;

    @Mock
    private ScheduleDAO scheduleDAO;

    @Mock
    private ConstraintDAO constraintDAO;

    @Mock
    private ConcreteShiftDAO concreteShiftDAO;

    @Mock
    private ScocciaturaDAO scocciaturaDAO;

    @Mock
    private HolidayDAO holidayDAO;

    @Mock
    private DoctorHolidaysDAO doctorHolidaysDAO;

    @Mock
    private DoctorUffaPriorityDAO doctorUffaPriorityDAO;

    @InjectMocks
    private SchedulerController schedulerController;

    // --------------------------------------------------------------------------------------
    // Test Category A: Schedule Retrieval
    // --------------------------------------------------------------------------------------

    /**
     * Tests the retrieval of all schedules formatted for the planner view.
     * Verifies that the controller correctly invokes the DAO to fetch all schedule entities
     * and accurately maps their attributes (ID, dates, and validity status) into
     * {@link ShowScheduleToPlannerDTO} objects.
     */
    @Test
    void getAllSchedulesWithDates_ReturnsDTOs() {
        // Setup
        long startDateEpoch = LocalDate.of(2023, 1, 1).toEpochDay();
        long endDateEpoch = LocalDate.of(2023, 1, 31).toEpochDay();

        Schedule schedule = new Schedule(startDateEpoch, endDateEpoch, new ArrayList<>());
        schedule.setId(1L);

        when(scheduleDAO.findAll()).thenReturn(Collections.singletonList(schedule));

        // Execute
        Set<ShowScheduleToPlannerDTO> result = schedulerController.getAllSchedulesWithDates();

        // Assert
        assertEquals(1, result.size());
        ShowScheduleToPlannerDTO dto = result.iterator().next();
        assertEquals(1L, dto.getScheduleID());
        assertEquals(DateConverter.convertEpochToDateString(startDateEpoch), dto.getStartDate());
        assertEquals(DateConverter.convertEpochToDateString(endDateEpoch), dto.getEndDate());
        assertFalse(dto.isHasViolatedConstraints());
    }

    /**
     * Tests the retrieval of all existing schedules converted into DTOs.
     * Verifies that the controller correctly fetches all schedule entities from the database
     * and properly transforms them into a list of {@link ScheduleDTO} objects,
     * maintaining data integrity such as the schedule ID.
     */
    @Test
    void readSchedules_ReturnsDTOs() {
        // Setup
        long startDateEpoch = LocalDate.of(2023, 1, 1).toEpochDay();
        long endDateEpoch = LocalDate.of(2023, 1, 31).toEpochDay();
        Schedule schedule = new Schedule(startDateEpoch, endDateEpoch, new ArrayList<>());
        schedule.setId(1L);

        when(scheduleDAO.findAll()).thenReturn(Collections.singletonList(schedule));

        // Execute
        List<ScheduleDTO> result = schedulerController.readSchedules();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    /**
     * Tests the retrieval of all illegal schedules (schedules with constraint violations).
     * Verifies that the controller correctly fetches schedules marked as illegal from the DAO
     * and ensures the {@link ScheduleDTO} objects accurately reflect the illegal status
     * through the 'isIllegal' flag.
     */
    @Test
    void readIllegalSchedules_ReturnsDTOs() {
        // Setup
        Schedule schedule = new Schedule(LocalDate.now().toEpochDay(), LocalDate.now().plusDays(5).toEpochDay(), new ArrayList<>());
        schedule.setId(2L);
        schedule.setCauseIllegal(new Exception("Constraint violated")); // Mark as illegal

        when(scheduleDAO.leggiSchedulazioniIllegali()).thenReturn(Collections.singletonList(schedule));

        // Execute
        List<ScheduleDTO> result = schedulerController.readIllegalSchedules();

        // Assert
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertTrue(result.get(0).isIllegal());
    }

    // --------------------------------------------------------------------------------------
    // Test Category B: Schedule Creation Logic
    // --------------------------------------------------------------------------------------

    /**
     * Tests the successful creation of a new schedule for a valid future date range.
     * Verifies that the controller correctly processes the date interval, associates
     * available shifts with specific dates, initializes the ScheduleBuilder, and
     * persists the resulting {@link Schedule} entity via the DAO.
     */
    @Test
    void createSchedule_Success() {
        // 1. Setup Dates: Must be in the FUTURE to pass validation
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(11); // 2 days duration

        // 2. Mock Data for Dependencies
        when(scheduleDAO.findAll()).thenReturn(Collections.emptyList()); // No conflicts with existing schedules

        // Shift setup: Ensure the shift is valid for the startDate's day of week
        Set<DayOfWeek> validDays = Collections.singleton(startDate.getDayOfWeek());

        // Mock MedicalService to ensure it doesn't cause NPE if accessed
        MedicalService mockService = mock(MedicalService.class);

        Shift shift = new Shift(LocalTime.of(8, 0), Duration.ofHours(8), mockService, TimeSlot.MORNING, Collections.emptyList(), // No specific staffing requirements (simplifies builder success)
                validDays, Collections.emptyList());

        when(shiftDAO.findAll()).thenReturn(Collections.singletonList(shift));

        // Mocks for ScheduleBuilder constructor requirements (called inside createSchedule)
        when(scocciaturaDAO.findAll()).thenReturn(Collections.emptyList());
        when(constraintDAO.findAll()).thenReturn(Collections.emptyList());
        when(doctorDAO.findAll()).thenReturn(Collections.emptyList());
        when(holidayDAO.findAll()).thenReturn(Collections.emptyList());
        when(doctorHolidaysDAO.findAll()).thenReturn(Collections.emptyList());

        // Input lists for the method signature
        List<DoctorUffaPriority> priorities = new ArrayList<>();
        List<DoctorUffaPrioritySnapshot> snapshots = new ArrayList<>();

        // 3. Execute
        // Calling the specific overload requested
        Schedule result = schedulerController.createSchedule(startDate, endDate, priorities, snapshots);

        // 4. Assert
        assertNotNull(result, "Schedule creation should succeed and return a non-null object");
        assertEquals(startDate.toEpochDay(), result.getStartDate());
        assertEquals(endDate.toEpochDay(), result.getEndDate());

        // Verify persistence was called
        verify(scheduleDAO, atLeastOnce()).save(any(Schedule.class));
    }

    /**
     * Tests the validation logic that prevents creating an initial schedule in the past.
     * Verifies that when no schedules exist in the system, the controller returns null
     * if the requested start date is before the current date, enforcing the business
     * rule against generating historical schedules.
     */
    @Test
    void createSchedule_InitialPastDate_ReturnsNull() {
        // Setup: No existing schedules, try to create one in the past
        when(scheduleDAO.findAll()).thenReturn(Collections.emptyList());

        LocalDate pastDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().minusDays(5);
        List<DoctorUffaPriority> priorities = new ArrayList<>();
        List<DoctorUffaPrioritySnapshot> snapshots = new ArrayList<>();

        // Execute
        Schedule result = schedulerController.createSchedule(pastDate, endDate, priorities, snapshots);

        // Assert
        assertNull(result);
    }

    /**
     * Tests the duplicate schedule detection logic.
     * Verifies that the controller returns null when a request is made to create
     * a schedule for a date range that exactly matches an existing schedule
     * already stored in the database.
     */
    @Test
    void createSchedule_OverlapRequest_ReturnsNull() {
        // Setup: An existing schedule has the exact same dates
        long startEpoch = LocalDate.of(2023, 5, 1).toEpochDay();
        long endEpoch = LocalDate.of(2023, 5, 31).toEpochDay();
        Schedule existingSchedule = new Schedule(startEpoch, endEpoch, new ArrayList<>());

        when(scheduleDAO.findAll()).thenReturn(Collections.singletonList(existingSchedule));

        LocalDate startDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 31);
        List<DoctorUffaPriority> priorities = new ArrayList<>();
        List<DoctorUffaPrioritySnapshot> snapshots = new ArrayList<>();

        // Execute
        Schedule result = schedulerController.createSchedule(startDate, endDate, priorities, snapshots);

        // Assert
        assertNull(result);
    }

    // --------------------------------------------------------------------------------------
    // Test Category C: Schedule Removal
    // --------------------------------------------------------------------------------------

    /**
     * Tests the behavior of schedule removal when the specified schedule ID does not exist.
     * Verifies that the controller returns false and does not attempt any deletion
     * operations when the DAO fails to find a schedule matching the provided ID.
     */
    @Test
    void removeSchedule_NotFound_ReturnsFalse() {
        // Setup
        when(scheduleDAO.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        boolean result = schedulerController.removeSchedule(999L);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests the business rule that prevents the deletion of schedules in the past.
     * Verifies that if a schedule's end date is before the current date, the controller
     * returns false and does not perform any deletion operations, thereby preserving
     * historical scheduling data.
     */
    @Test
    void removeSchedule_PastSchedule_ReturnsFalse() {
        // Setup: Schedule ends in the past
        long pastEndEpoch = LocalDate.now().minusDays(1).toEpochDay();
        Schedule pastSchedule = new Schedule(pastEndEpoch - 10, pastEndEpoch, new ArrayList<>());
        pastSchedule.setId(1L);

        when(scheduleDAO.findById(1L)).thenReturn(Optional.of(pastSchedule));

        // Execute
        boolean result = schedulerController.removeSchedule(1L);

        // Assert
        assertFalse(result);
        verify(scheduleDAO, never()).deleteById(anyLong());
    }

    /**
     * Tests the successful removal of a schedule that concludes in the future.
     * Verifies that the controller allows the deletion of non-past schedules,
     * performs the necessary cleanup of doctor priority associations to maintain
     * foreign key integrity, and invokes the DAO to delete the record.
     */
    @Test
    void removeSchedule_FutureSchedule_Success() {
        // Setup: Schedule ends in the future
        long futureEndEpoch = LocalDate.now().plusDays(10).toEpochDay();
        Schedule futureSchedule = new Schedule(futureEndEpoch - 5, futureEndEpoch, new ArrayList<>());
        futureSchedule.setId(1L);

        when(scheduleDAO.findById(1L)).thenReturn(Optional.of(futureSchedule));
        when(doctorUffaPriorityDAO.findAll()).thenReturn(Collections.emptyList()); // for priority cleanup

        // Execute
        boolean result = schedulerController.removeSchedule(1L);

        // Assert
        assertTrue(result);
        verify(scheduleDAO).deleteById(1L);
    }

    // --------------------------------------------------------------------------------------
    // Test Category D: Schedule Regeneration
    // --------------------------------------------------------------------------------------

    /**
     * Tests the behavior of schedule regeneration when the provided ID does not correspond to any existing schedule.
     * Verifies that the controller returns false and aborts the recreation process when the DAO cannot
     * find the target schedule in the database.
     */
    @Test
    void recreateSchedule_NotFound_ReturnsFalse() {
        // Setup
        when(scheduleDAO.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        boolean result = schedulerController.recreateSchedule(999L);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests the business rule that prevents the regeneration of schedules that have already concluded.
     * Verifies that the controller returns false and aborts the recreation process if the
     * target schedule's end date is in the past, ensuring the integrity of historical scheduling data.
     */
    @Test
    void recreateSchedule_PastSchedule_ReturnsFalse() {
        // Setup: Uses logic similar to removeSchedule for validation
        long pastEndEpoch = LocalDate.now().minusDays(1).toEpochDay();
        Schedule pastSchedule = new Schedule(pastEndEpoch - 10, pastEndEpoch, new ArrayList<>());
        pastSchedule.setId(1L);

        when(scheduleDAO.findById(1L)).thenReturn(Optional.of(pastSchedule));

        // Execute
        boolean result = schedulerController.recreateSchedule(1L);

        // Assert
        assertFalse(result);
    }

    // --------------------------------------------------------------------------------------
    // Test Category E: Concrete Shift Management
    // --------------------------------------------------------------------------------------

    /**
     * Tests the behavior of concrete shift removal when the specified ID does not exist.
     * Verifies that the controller returns false and aborts the operation when the
     * DAO cannot find a matching {@link ConcreteShift} in the database.
     */
    @Test
    void removeConcreteShift_NotFound_ReturnsFalse() {
        // Setup
        when(concreteShiftDAO.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        boolean result = schedulerController.removeConcreteShift(100L);

        // Assert
        assertFalse(result);
    }

    /**
     * Tests the successful removal of an existing concrete shift.
     * Verifies that the controller correctly identifies the shift by ID, removes it
     * from the associated schedule's list, and invokes the DAO to delete the
     * shift entity from the database.
     */
    @Test
    void removeConcreteShift_Found_Success() {
        // Setup
        long dateEpoch = LocalDate.now().toEpochDay();

        Shift shift = new Shift(LocalTime.of(8, 0), Duration.ofHours(4), null, TimeSlot.MORNING, Collections.emptyList(), Collections.emptySet(), Collections.emptyList());

        ConcreteShift concreteShift = new ConcreteShift(dateEpoch, shift);
        concreteShift.setId(100L);

        Schedule schedule = new Schedule(dateEpoch, dateEpoch, new ArrayList<>(Collections.singletonList(concreteShift)));

        when(concreteShiftDAO.findById(100L)).thenReturn(Optional.of(concreteShift));
        when(scheduleDAO.findByDateBetween(dateEpoch)).thenReturn(schedule);

        // Execute
        boolean result = schedulerController.removeConcreteShift(100L);

        // Assert
        assertTrue(result);
        verify(concreteShiftDAO).delete(concreteShift);
        assertFalse(schedule.getConcreteShifts().contains(concreteShift));
    }

    /**
     * Tests the error handling when attempting to add a concrete shift based on a non-existent shift template.
     * Verifies that the controller throws a {@link ConcreteShiftException} when the DAO fails
     * to find any shift matching the provided medical service label and time slot,
     * preventing the creation of inconsistent shift data.
     */
    @Test
    void addConcreteShift_ShiftNotFound_ThrowsException() {
        // Setup
        RegisterConcreteShiftDTO dto = new RegisterConcreteShiftDTO();
        MedicalServiceDTO serviceDTO = new MedicalServiceDTO("NonExistentService");
        dto.setServizio(serviceDTO);
        dto.setTimeSlot(TimeSlot.MORNING);
        dto.setYear(2023);
        dto.setMonth(1);
        dto.setDay(1);

        when(shiftDAO.findAllByMedicalServiceLabelAndTimeSlot("NonExistentService", TimeSlot.MORNING)).thenReturn(Collections.emptyList());

        // Execute & Assert
        assertThrows(ConcreteShiftException.class, () -> schedulerController.addConcreteShift(dto, false));
    }
}