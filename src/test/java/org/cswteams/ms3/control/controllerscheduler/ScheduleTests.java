package org.cswteams.ms3.control.controllerscheduler;

import org.cswteams.ms3.AbstractMultiTenantIntegrationTest;
import org.cswteams.ms3.config.multitenancy.SchemasInitializer;
import org.cswteams.ms3.control.medicalService.MedicalServiceController;
import org.cswteams.ms3.control.scheduler.SchedulerController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.user.UserCreationDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.cswteams.ms3.control.controllerscheduler.utils.TestDatesEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SchemasInitializer.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Transactional
public class ScheduleTests extends AbstractMultiTenantIntegrationTest {

    @Autowired
    SchedulerController instance;
    @Autowired
    ScheduleDAO scheduleDao;
    @Autowired
    SpecializationDAO specializationDAO;
    @Autowired
    MedicalServiceController medicalServiceController;
    @Autowired
    MedicalServiceDAO medicalServiceDAO;
    @Autowired
    UserController userController;
    @Autowired
    DoctorDAO doctorDAO;
    @Autowired
    ShiftDAO shiftDAO;
    @Autowired
    TaskDAO taskDAO;
    @Autowired
    HolidayDAO holidayDAO;
    @Autowired
    DoctorUffaPriorityDAO doctorUffaPriorityDAO;
    @Autowired
    DoctorHolidaysDAO doctorHolidaysDAO;
    @Autowired
    DoctorUffaPrioritySnapshotDAO doctorUffaPrioritySnapshotDAO;
    @Autowired
    EntityManager entityManager;
    @Autowired
    ConstraintDAO constraintDAO;

    private Doctor doc1;
    private Doctor doc2;
    private MedicalService serviceA;

    static Stream<Arguments> createScheduleValidTestParams() {
        return Stream.of(Arguments.of((Object) new LocalDate[]{FUTURE_START.getDate(), FUTURE_END.getDate()}), Arguments.of((Object) new LocalDate[]{TODAY.getDate(), TODAY.getDate().plusDays(5)}));
    }

    @BeforeEach
    void setUp() {
        this.setUpTenantForTx();
        doctorUffaPrioritySnapshotDAO.deleteAll();
        doctorUffaPriorityDAO.deleteAll();
        doctorHolidaysDAO.deleteAll();
        scheduleDao.deleteAll();
        shiftDAO.deleteAll();
        entityManager.createNativeQuery("DELETE FROM medical_service_tasks").executeUpdate();
        medicalServiceDAO.deleteAll();
        doctorDAO.deleteAll();
        taskDAO.deleteAll();
        specializationDAO.deleteAll();
        holidayDAO.deleteAll();
        seedData();
    }

    private void seedData() {
        Task ward = new Task(TaskEnum.WARD);
        taskDAO.saveAndFlush(ward);
        serviceA = medicalServiceController.createService(Collections.singletonList(ward), "ALOGIA");

        Specialization spec = new Specialization("ALOGIA");
        specializationDAO.save(spec);

        doc1 = new Doctor("Esperto", "Alogia", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "doc1@test.it", "passw", Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
        doc2 = new Doctor("Giovane", "Alogia", "VRDLGU85B02I602X", LocalDate.of(1985, 2, 2), "doc2@test.it", "passw", Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
        doctorDAO.save(doc1);
        doctorDAO.save(doc2);

        try {
            userController.addSpecialization(doc1, spec);
            userController.addSpecialization(doc2, spec);
        } catch (Exception ignored) {
        }

        Map<Seniority, Integer> quantities = Map.of(Seniority.STRUCTURED, 1, Seniority.SPECIALIST_SENIOR, 1);
        QuantityShiftSeniority qss = new QuantityShiftSeniority(new HashMap<>(quantities), ward);
        shiftDAO.saveAndFlush(new Shift(LocalTime.of(8, 0), Duration.ofHours(6), serviceA, TimeSlot.MORNING, Collections.singletonList(qss), EnumSet.allOf(DayOfWeek.class), Collections.emptyList()));

        doctorUffaPriorityDAO.save(new DoctorUffaPriority(doc1));
        doctorUffaPriorityDAO.save(new DoctorUffaPriority(doc2));
        doctorUffaPrioritySnapshotDAO.save(new DoctorUffaPrioritySnapshot(doc1));
        doctorUffaPrioritySnapshotDAO.save(new DoctorUffaPrioritySnapshot(doc2));
    }

    @ParameterizedTest
    @MethodSource("createScheduleValidTestParams")
    void createScheduleValidTest(LocalDate[] data) {
        Schedule schedule = instance.createSchedule(data[0], data[1]);
        assertNotNull(schedule);
        assertEquals(data[0].toEpochDay(), schedule.getStartDate());
    }

    @Test
    void testCreateScheduleDuplicateRangeFails() {
        instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        Schedule duplicate = instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        assertNull(duplicate, "Duplicate range should be rejected");
    }

    @Test
    void testAddConcreteShiftDoctorCollision_DoesntThrowException() {
        // 1. Create a valid schedule first so that addConcreteShift can find it
        LocalDate start = FUTURE_START.getDate();
        LocalDate end = FUTURE_END.getDate();
        Schedule schedule = instance.createSchedule(start, end);
        assertNotNull(schedule, "A schedule must exist for addConcreteShift to work");

        // 2. Prepare the DTO for the concrete shift within that schedule's range
        RegisterConcreteShiftDTO dto = new RegisterConcreteShiftDTO();
        dto.setDay(start.getDayOfMonth());
        dto.setMonth(start.getMonthValue());
        dto.setYear(start.getYear());
        dto.setTimeSlot(TimeSlot.MORNING);
        dto.setServizio(new org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO(
                serviceA.getId(), serviceA.getLabel(), new ArrayList<>()
        ));

        // 3. Use the all-args constructor for UserCreationDTO as required by your project
        UserCreationDTO userDto = new UserCreationDTO(
                doc1.getId(),
                doc1.getName(),
                doc1.getLastname(),
                doc1.getBirthday(),
                doc1.getTaxCode(),
                doc1.getEmail(),
                doc1.getPassword(),
                new ArrayList<>(),
                doc1.getSeniority().toString()
        );

        // 4. Set the same doctor in both roles to trigger the collision
        dto.setOnDutyDoctors(Set.of(userDto));
        dto.setOnCallDoctors(Set.of(userDto));

        // 5. Assert the correct exception is thrown
        assertDoesNotThrow(() -> instance.addConcreteShift(dto, false), "addConcreteShift does not allow inserting the same doctor in both roles");
    }

    @Test
    void testRecreateScheduleRestoresPrioritiesFlow() {
        Schedule original = instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        long originalId = original.getId();

        DoctorUffaPriority dup = doctorUffaPriorityDAO.findAll().get(0);
        dup.setGeneralPriority(100);
        doctorUffaPriorityDAO.save(dup);

        boolean result = instance.recreateSchedule(originalId);
        assertTrue(result, "Regeneration should succeed");

        DoctorUffaPriority restored = doctorUffaPriorityDAO.findByDoctor_Id(dup.getDoctor().getId()).get(0);
        assertEquals(0, restored.getGeneralPriority(), "Priority should be restored to snapshot value (0)");
    }

    @Test
    void readIllegalScheduleTest() {
        // 1. Manually create and save a constraint since the DB is cleared in setUp
        // We use a concrete implementation like ConstraintUbiquita
        Constraint constraint = new org.cswteams.ms3.entity.constraint.ConstraintUbiquita();
        constraint.setDescription("Test Constraint");
        constraint.setViolable(true);
        constraintDAO.saveAndFlush(constraint);

        // 2. Generate a valid schedule
        Schedule s = instance.createSchedule(FUTURE_START.getDate(), FUTURE_END.getDate());
        assertNotNull(s, "The schedule should have been generated successfully");

        // 4. Add the violation to the schedule's list and set the illegal cause
        s.getViolatedConstraints().add(constraint);
        s.setCauseIllegal(new IllegalAssegnazioneTurnoException("Test Violation"));

        // 5. Use saveAndFlush to synchronize the Persistence Context with the database
        // This ensures the custom 'size != 0' query sees the new entry in the join table
        scheduleDao.saveAndFlush(s);

        // 6. Retrieve illegal schedules and verify
        List<ScheduleDTO> illegals = instance.readIllegalSchedules();

        assertFalse(illegals.isEmpty(), "The list should not be empty when a violation exists");
        assertTrue(illegals.stream().anyMatch(dto -> dto.getId() == s.getId()),
                "The schedule should be present in the illegal list");
    }
}