package org.cswteams.ms3.control.scheduler;

import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/***********************************************************************************
 * This class has the responsibility of testing the constructor method of the      *
 * class ScheduleBuilder. In particular the code is divided in 4 different parts   *
 * -  SETUP                                                                        *
 * -  DOMAIN PARTITION                                                             *
 * -  FIRST CONSTRUCTOR TEST                                                       *
 * -  SECOND CONSTRUCTOR TEST                                                      *
 ***********************************************************************************/
@SpringBootTest
public class ScheduleBuilderBuildTest extends ScheduleBuilderTest {
    private static final Logger log = LoggerFactory.getLogger(ScheduleBuilderBuildTest.class);
    private static List<AssegnazioneTurno> listOfNoUserShift;
    private static List<AssegnazioneTurno> listOfUserShift;
    private static List<Vincolo> noConstraints;
    private static List<Vincolo> correctConstraints;
    private static List<Vincolo> violatedConstraints;
    private static List<Utente> userList;
    private static List<Utente> emptyUserList;
    private static Schedule schedule;
    private static Schedule ilelaglSchedule;

    /**************************************************************
     *                          SETUP                             *
     *************************************************************/

    @BeforeAll
    static void setUp() {
        log.info("[TEST] Starting ScheduleBuilderConstructorTest...");
        // Mock initialization

        // Shift without any user
        AssegnazioneTurno noUserAssignmentShift = mock(AssegnazioneTurno.class);
        // Shift with only one user, which is a "SPECIALIZZANDO"
        AssegnazioneTurno assigmentShift = mock(AssegnazioneTurno.class);
        Turno dummyShift = mock(Turno.class);
        Turno dummyNoUserShift = mock(Turno.class);


        log.info("[DEBUG] [TEST] Setup for first partition started");

        // Initialize variable to return in the mocks returns
        List<RuoloNumero> roleList = new ArrayList<>();
        List<RuoloNumero> emptyRoleList = new ArrayList<>();
        Utente user = new Utente(
                278831L,
                "Simone",
                "Staccone",
                "STCASMO00O",
                LocalDate.of(2000,8,16),
                "simone.staccone@virgilio.it",
                "psw",
                RuoloEnum.SPECIALIZZANDO,
                AttoreEnum.UTENTE
        );
        Vincolo violatedConstraint = new Vincolo() {
            @Override
            public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                throw new ViolatedConstraintException();
            }
        };

        // Global variable initialization (all the variables that will be used in the tests)
        noConstraints = new ArrayList<>();
        correctConstraints = new ArrayList<>();
        violatedConstraints = new ArrayList<>();
        userList = new ArrayList<>();
        emptyUserList = new ArrayList<>();

        // Initialize users in the system
        userList.add(user);
        roleList.add(new RuoloNumero(RuoloEnum.SPECIALIZZANDO,1));

        // Initialize shift list
        listOfNoUserShift = new ArrayList<>();
        listOfUserShift = new ArrayList<>();

        listOfUserShift.add(assigmentShift);
        listOfNoUserShift.add(noUserAssignmentShift);

        // Initialize constraints in the system
        correctConstraints.add(new Vincolo() {
            @Override
            public void verificaVincolo(ContestoVincolo contesto){
            }
        });


        violatedConstraints.add(violatedConstraint);





        // Mock assignment shifts
        when(noUserAssignmentShift.getUtenti()).thenReturn(new HashSet<>());
        when(assigmentShift.getUtenti()).thenReturn(new HashSet<>(userList));

        when(noUserAssignmentShift.getTurno()).thenReturn(dummyNoUserShift);
        when(assigmentShift.getTurno()).thenReturn(dummyShift);

        when(noUserAssignmentShift.getUtenti()).thenReturn(null);
        when(assigmentShift.getUtenti()).thenReturn(new HashSet<>(userList));

        when(noUserAssignmentShift.getUtentiReperibili()).thenReturn(null);
        when(assigmentShift.getUtentiReperibili()).thenReturn(new HashSet<>(userList));

        when(noUserAssignmentShift.getUtentiDiGuardia()).thenReturn(null);
        when(assigmentShift.getUtentiDiGuardia()).thenReturn(new HashSet<>(userList));

        when(assigmentShift.getData()).thenReturn(LocalDate.of(2023,11,23));
        when(noUserAssignmentShift.getData()).thenReturn(LocalDate.of(2023,11,23));

        // Mock shifts
        when(dummyNoUserShift.getRuoliNumero()).thenReturn(emptyRoleList);
        when(dummyShift.getRuoliNumero()).thenReturn(roleList);

        when(dummyNoUserShift.getOraInizio()).thenReturn(LocalTime.MIDNIGHT);
        when(dummyShift.getOraInizio()).thenReturn(LocalTime.MIDNIGHT);

        log.info("[DEBUG] [TEST] Setup for first partition correctly finished");

        log.info("[DEBUG] [TEST] Setup for second partition started");

        // Mock initialization
        schedule = mock(Schedule.class);
        ilelaglSchedule = mock(Schedule.class);

        when(schedule.isIllegal()).thenReturn(false);
        when(ilelaglSchedule.isIllegal()).thenReturn(true);

        when(schedule.getStartDate()).thenReturn(LocalDate.of(2023,11,23));
        when(schedule.getEndDate()).thenReturn(LocalDate.of(2023,11,27));
        when(ilelaglSchedule.getStartDate()).thenReturn(null);
        when(ilelaglSchedule.getEndDate()).thenReturn(null);


        log.info("[DEBUG] [TEST] Setup for seconda partition correctly finished");
    }

    @AfterAll
    static void cleanUp() {
        listOfNoUserShift = null;
        listOfUserShift = null;
        noConstraints = null;
        correctConstraints = null;
        violatedConstraints = null;
        userList = null;
        emptyUserList = null;
        schedule = null;
        ilelaglSchedule = null;

        log.info("[TEST] Test ScheduleBuilderConstructorTest finished");
    }

    /*************************************************************
     *                    DOMANI PARTITION                       *
     *************************************************************/

    /* *********************************************************************
     * DOMAIN PARTITIONING FOR FIRST CONSRTUCTOR
     * () -> (startDate, endDate, constraints, assignedShifts, users, expectedException)
     * startDate/endDate : {startDate < endDate}, {startDate >= actualDate}, {startDate >= endDate}, {startDate < actualDate}
     * constraints : {correct constraint}, {violated constraint}, {no constraints} , {null}
     * assignedShifts: {empty list}, {full list}, {null}
     * users : {list of user}, {empty list of users}, {null}
     *
     * This is a multidimensional partitioning (using above blocks to get cartesian product of the sets of inputs)
     * **********************************************************************/
    private static Stream<Arguments> firstConstructorPartition() {
        return Stream.of(
                // Date partition
                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, userList, false),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(1),LocalDate.now(),correctConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(1),correctConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfUserShift, userList, true),

                // Add constraint partition
                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, userList, false),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, userList, false),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfUserShift, userList, true),


                // Add assignedShift partition
                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfNoUserShift, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfNoUserShift, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfNoUserShift, userList, true),



                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints, null, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints, null, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints, null, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints, null, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints, null, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints, null, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints, null, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints, null, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints, null, userList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null, null, userList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null, null, userList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null, null, userList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null, null, userList, true),



                // Add userList partition
                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfUserShift, emptyUserList, true),



                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfNoUserShift, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfNoUserShift, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfNoUserShift, emptyUserList, true),



                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints, null, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints, null, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints, null, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints, null, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints, null, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints, null, emptyUserList, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null, null, emptyUserList, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null, null, emptyUserList, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null, null, emptyUserList, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null, null, emptyUserList, true),







                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints,listOfUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfUserShift, null, true),



                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints,listOfNoUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints,listOfNoUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints,listOfNoUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints,listOfNoUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints,listOfNoUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints,listOfNoUserShift, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null,listOfNoUserShift, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null,listOfNoUserShift, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null,listOfNoUserShift, null, true),



                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),correctConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),correctConstraints, null, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),correctConstraints, null, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),correctConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),correctConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),correctConstraints, null, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),violatedConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),violatedConstraints, null, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),violatedConstraints, null, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),violatedConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),violatedConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),violatedConstraints, null, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),noConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),noConstraints, null, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),noConstraints, null, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),noConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),noConstraints, null, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),noConstraints, null, null, true),

                Arguments.of(LocalDate.now(),LocalDate.now().plusDays(7),null, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().plusDays(7),null, null, null, true), // Actual date after start date, should throw an error
                Arguments.of(LocalDate.now(),LocalDate.now(),null, null, null, true),
                Arguments.of(LocalDate.now().plusDays(7),LocalDate.now(),null, null, null, true),
                Arguments.of(LocalDate.now().minusDays(2),LocalDate.now().minusDays(2),null, null, null, true),
                Arguments.of(LocalDate.now().minusDays(1),LocalDate.now().minusDays(2),null, null, null, true)



                );
    }

    /* *********************************************************************
     * DOMAIN PARTITIONING FOR SECOND CONSTRUCTOR
     * () -> (constraints, users, schedule ,expectedException)
     * constraints : {correct constraint}, {violated constraint}, {no constraints} , {null}
     * users : {list of user}, {empty list of users}, {null}
     * schedule : {valid schedule}, {illegal schedule}, {null}
     *
     * This is a multidimensional partitioning (using above blocks to get cartesian product of the sets of inputs)
     * **********************************************************************/
    private static Stream<Arguments> secondConstructorPartition() {
        return Stream.of(
                //Add constraint partition
                Arguments.of(correctConstraints,userList,schedule, false),
                Arguments.of(violatedConstraints,userList,schedule, false),
                Arguments.of(noConstraints,userList,schedule, false),
                Arguments.of(null,userList,schedule, true),

                //Add users constraints
                Arguments.of(correctConstraints,emptyUserList,schedule, false),
                Arguments.of(violatedConstraints,emptyUserList,schedule, false),
                Arguments.of(noConstraints,emptyUserList,schedule, false),
                Arguments.of(null,userList,schedule, true),

                Arguments.of(correctConstraints,null,schedule, true),
                Arguments.of(violatedConstraints,null,schedule, true),
                Arguments.of(noConstraints,null,schedule, true),
                Arguments.of(null,null,schedule, true),


                // Add schedule constraints
                Arguments.of(correctConstraints,userList,ilelaglSchedule, true),
                Arguments.of(violatedConstraints,userList,ilelaglSchedule, true),
                Arguments.of(noConstraints,userList,ilelaglSchedule, true),
                Arguments.of(null,userList,ilelaglSchedule, true),

                Arguments.of(correctConstraints,emptyUserList,ilelaglSchedule, true),
                Arguments.of(violatedConstraints,emptyUserList,ilelaglSchedule, true),
                Arguments.of(noConstraints,emptyUserList,ilelaglSchedule, true),
                Arguments.of(null,emptyUserList, ilelaglSchedule ,true),

                Arguments.of(correctConstraints,null,ilelaglSchedule, true),
                Arguments.of(violatedConstraints,null,ilelaglSchedule, true),
                Arguments.of(noConstraints,null,ilelaglSchedule, true),
                Arguments.of(null,null,ilelaglSchedule, true),



                Arguments.of(correctConstraints,userList,null, true),
                Arguments.of(violatedConstraints,userList,null, true),
                Arguments.of(noConstraints,userList,null, true),
                Arguments.of(null,userList,null, true),

                Arguments.of(correctConstraints,emptyUserList,null, true),
                Arguments.of(violatedConstraints,emptyUserList,null, true),
                Arguments.of(noConstraints,emptyUserList,null, true),
                Arguments.of(null,emptyUserList,null , true),

                Arguments.of(correctConstraints,null,schedule, true),
                Arguments.of(violatedConstraints,null,schedule, true),
                Arguments.of(noConstraints,null,schedule, true),
                Arguments.of(null,null,schedule, true)


        );
    }

    /**************************************************************
     *                   FIRST CONSTRUCTOR TEST                   *
     *************************************************************/

    @ParameterizedTest
    @MethodSource("firstConstructorPartition")
    public void firstConstructorTest(
            LocalDate startDate,
            LocalDate endDate,
            List<Vincolo> constraints,
            List<AssegnazioneTurno> allAssignedShifts,
            List<Utente> users,
            boolean expectedException
        ) {
        // Arrange
        ScheduleBuilder scheduleBuilder;

        if (!expectedException) {
            assertDoesNotThrow(() -> new ScheduleBuilder(startDate, endDate, constraints, allAssignedShifts, users));
            try {
                scheduleBuilder = new ScheduleBuilder(startDate, endDate, constraints, allAssignedShifts, users);
                // Act
                Schedule resultSchedule = scheduleBuilder.build();

                // Assert
                assertEquals(startDate, resultSchedule.getStartDate());
                assertEquals(endDate, resultSchedule.getEndDate());

                if(!resultSchedule.getViolatedConstraintLog().isEmpty()) {
                    assertEquals(resultSchedule.getViolatedConstraintLog().size(),constraints.size());
                }
            } catch (IllegalScheduleException e) {
                throw new RuntimeException(e);
            }

        } else {
            assertThrows(Exception.class, () -> new ScheduleBuilder(startDate, endDate, constraints, allAssignedShifts, users));

        }
    }

    /**************************************************************
     *                  SECOND CONSTRUCTOR TEST                   *
     *************************************************************/

    @ParameterizedTest
    @MethodSource("secondConstructorPartition")
    public void secondConstructorTest(
            List<Vincolo> constraints,
            List<Utente> users,
            Schedule schedule,
            boolean expectedException
    ) {
        ScheduleBuilder scheduleBuilder;

        if (!expectedException) {
            assertDoesNotThrow(() -> new ScheduleBuilder(constraints, users, schedule));

            try {
                scheduleBuilder = new ScheduleBuilder(constraints, users, schedule);
                // Act
                Schedule resultSchedule = scheduleBuilder.build();

                // Assert
                assertEquals(schedule.getStartDate(), resultSchedule.getStartDate());
                assertEquals(schedule.getEndDate(), resultSchedule.getEndDate());

                if(!resultSchedule.getViolatedConstraintLog().isEmpty()) {
                    assertEquals(resultSchedule.getViolatedConstraintLog().size(),constraints.size());
                }

            } catch (IllegalScheduleException e) {
                throw new RuntimeException(e);
            }

        } else {
            assertThrows(Exception.class, () -> new ScheduleBuilder(constraints, users, schedule));

        }

    }

}