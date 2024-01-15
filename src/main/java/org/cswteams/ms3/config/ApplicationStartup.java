package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.CalendarSettingBuilder;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.scheduler.ScheduleBuilder;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.condition.*;
import org.cswteams.ms3.entity.constraint.*;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.entity.scocciature.ScocciaturaAssegnazioneUtente;
import org.cswteams.ms3.entity.scocciature.ScocciaturaDesiderata;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.scocciature.ScocciaturaVacanza;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.ShiftException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


import javax.print.Doc;
import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component()
@Profile("!test")
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ICalendarServiceManager calendarServiceManager;

    @Autowired
    private DoctorDAO doctorDAO;

    @Autowired
    private ShiftDAO shiftDAO;

    @Autowired
    private MedicalServiceDAO medicalServiceDAO;

    @Autowired
    private PermanentConditionDAO permanentConditionDAO;

    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO;

    @Autowired
    private SpecializationDAO specializationDAO;

    @Autowired
    private ConstraintDAO constraintDAO;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private ScocciaturaDAO scocciaturaDAO;

    @Autowired
    private PreferenceDAO preferenceDAO;

    @Autowired
    private ConfigVincoliDAO configVincoliDAO;

    @Autowired
    private ConfigVincoloMaxPeriodoConsecutivoDAO configVincoloMaxPeriodoConsecutivoDAO;

    @Autowired
    private IMedicalServiceController medicalServiceController;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private DoctorAssignmentDAO doctorAssignmentDAO;

    @Autowired
    private HolidayDAO holidayDAO;

    @Autowired
    private DoctorUffaPriorityDAO doctorUffaPriorityDAO;

    @Autowired
    private DoctorHolidaysDAO doctorHolidaysDAO;



    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
      //  if (doctorDAO.count() == 0) {
            try {
                populateDB();
            } catch (ShiftException e) {
                e.printStackTrace();
            }

            //registerConstraints();
            registerScocciature();
     //   }

    }


    /**
     * Function which has the responsibility to register the holidays into the system, if it has not been registered yet.
     * @return List of HolidayDTO describing all the default holidays registered in the system.
     */
    private List<HolidayDTO> registerHolidays() {
        CalendarSettingBuilder settingBuilder = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
        List<HolidayDTO> holidays = holidayController.readHolidays();

        if(holidays.isEmpty()) {
            CalendarSetting setting = settingBuilder.create(String.valueOf(LocalDate.now().getYear()), "IT");
            calendarServiceManager.init(setting);

            try {
                holidays = calendarServiceManager.getHolidays();
            } catch (CalendarServiceException e) {
                e.printStackTrace();
            }
            holidayController.registerHoliday(holidays);

        }
        return holidays;

    }


    private void registerScocciature() {
        List<HolidayDTO> holidaysDTO = registerHolidays(); //TODO: HolidayDTO will be useful for uffa priority differentiation.

        //We are reasoning about 40 priority levels.
        int uffaPriorityPreference = 10;
        int uffaPriorityRespectedPreference = -1;   //TODO: introduce somehow a mechanism for reducing priority levels not only through periodic balancing

        int uffaPrioritySundayAfternoon = 1;
        int uffaPrioritySundayMorning = 1;
        int uffaPrioritySaturdayNight = 2;

        int uffaPrioritySaturdayAfternoon = 0;
        int uffaPrioritySaturdayMorning = 0;
        int uffaPriorityFridayNight = 1;
        int uffaPrioritySundayNight = 2;

        int uffaPriorityFridayAfternoon = 0;

        int uffaPrioritySimple = 0;
        int uffaPriorityNight = 1;

        int uffaPriorityHoliday = 4;  //TODO: customize uffa priority for different holidays and different TimeSlots
        int uffaPriorityHolidayNight = 5;

        Scocciatura scocciaturaSundayMorning = new ScocciaturaAssegnazioneUtente(uffaPrioritySundayMorning, DayOfWeek.SUNDAY, TimeSlot.MORNING);
        Scocciatura scocciaturaSundayAfternoon = new ScocciaturaAssegnazioneUtente(uffaPrioritySundayAfternoon, DayOfWeek.SUNDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaSundayNight = new ScocciaturaAssegnazioneUtente(uffaPrioritySundayNight, DayOfWeek.SUNDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaSaturdayMorning = new ScocciaturaAssegnazioneUtente(uffaPrioritySaturdayMorning, DayOfWeek.SATURDAY, TimeSlot.MORNING);
        Scocciatura scocciaturaSaturdayAfternoon = new ScocciaturaAssegnazioneUtente(uffaPrioritySaturdayAfternoon, DayOfWeek.SATURDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaSaturdayNight = new ScocciaturaAssegnazioneUtente(uffaPrioritySaturdayNight, DayOfWeek.SATURDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaFridayAfternoon = new ScocciaturaAssegnazioneUtente(uffaPriorityFridayAfternoon, DayOfWeek.FRIDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaFridayNight = new ScocciaturaAssegnazioneUtente(uffaPriorityFridayNight, DayOfWeek.FRIDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaPreference = new ScocciaturaDesiderata(uffaPriorityPreference);
        Scocciatura scocciaturaRespectedPreference = new ScocciaturaDesiderata(uffaPriorityRespectedPreference);

        scocciaturaDAO.save(scocciaturaSundayMorning);
        scocciaturaDAO.save(scocciaturaSundayAfternoon);
        scocciaturaDAO.save(scocciaturaSundayNight);

        scocciaturaDAO.save(scocciaturaSaturdayMorning);
        scocciaturaDAO.save(scocciaturaSaturdayAfternoon);
        scocciaturaDAO.save(scocciaturaSaturdayNight);

        scocciaturaDAO.save(scocciaturaFridayAfternoon);
        scocciaturaDAO.save(scocciaturaFridayNight);

        scocciaturaDAO.save(scocciaturaPreference);
        scocciaturaDAO.save(scocciaturaRespectedPreference);

        List<DayOfWeek> giorniFeriali = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        for (DayOfWeek giornoFeriale : giorniFeriali) {
            ScocciaturaAssegnazioneUtente scocciaturaFerialeMattina = new ScocciaturaAssegnazioneUtente(uffaPrioritySimple, giornoFeriale, TimeSlot.MORNING);
            scocciaturaDAO.save(scocciaturaFerialeMattina);
            if (giornoFeriale != DayOfWeek.FRIDAY) {
                ScocciaturaAssegnazioneUtente scocciaturaFerialePomeriggio = new ScocciaturaAssegnazioneUtente(uffaPrioritySimple, giornoFeriale, TimeSlot.AFTERNOON);
                scocciaturaDAO.save(scocciaturaFerialePomeriggio);
                ScocciaturaAssegnazioneUtente scocciaturaFerialeNotturno = new ScocciaturaAssegnazioneUtente(uffaPriorityNight, giornoFeriale, TimeSlot.NIGHT);
                scocciaturaDAO.save(scocciaturaFerialeNotturno);
            }
        }

        //retrieve of holiday entities (and not DTOs)
        List<Holiday> holidays = holidayDAO.findAll();

        for(Holiday holiday: holidays) {
            Scocciatura scocciaturaHolidayMorning = new ScocciaturaVacanza(uffaPriorityHoliday, holiday, TimeSlot.MORNING);
            Scocciatura scocciaturaHolidayAfternoon = new ScocciaturaVacanza(uffaPriorityHoliday, holiday, TimeSlot.AFTERNOON);
            Scocciatura scocciaturaHolidayNight = new ScocciaturaVacanza(uffaPriorityHolidayNight, holiday, TimeSlot.NIGHT);

            scocciaturaDAO.save(scocciaturaHolidayMorning);
            scocciaturaDAO.save(scocciaturaHolidayAfternoon);
            scocciaturaDAO.save(scocciaturaHolidayNight);

        }

    }


    private void registerConstraints() {

        ConfigVincoli configVincoli;
        try {
            File file = new File("src/main/resources/configVincoliDefault.properties");
            FileInputStream propsInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(propsInput);

            ConfigVincMaxPerCons confOver62 = new ConfigVincMaxPerCons(permanentConditionDAO.findByType("OVER_62"), Integer.parseInt(prop.getProperty("numMaxOreConsecutiveOver62")) * 60);
            ConfigVincMaxPerCons confIncinta = new ConfigVincMaxPerCons(temporaryConditionDAO.findByType("INCINTA"), Integer.parseInt(prop.getProperty("numMaxOreConsecutiveDonneIncinta")) * 60);
            configVincoloMaxPeriodoConsecutivoDAO.saveAndFlush(confOver62);
            configVincoloMaxPeriodoConsecutivoDAO.saveAndFlush(confIncinta);
            configVincoli = new ConfigVincoli(
                    Integer.parseInt(prop.getProperty("numGiorniPeriodo")),
                    Integer.parseInt(prop.getProperty("maxOrePeriodo")) * 60,
                    Integer.parseInt(prop.getProperty("HorizonTurnoNotturno")),
                    Integer.parseInt(prop.getProperty("numMaxOreConsecutivePerTutti")) * 60,
                    Arrays.asList(confOver62, confIncinta)
            );
            configVincoliDAO.save(configVincoli);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // nessun turno può essere allocato a questa persona durante il suo smonto notte
        ConstraintTipologieTurniContigue constraintConsecutiveShifts = new ConstraintTipologieTurniContigue(
                configVincoli.getHorizonTurnoNotturno(),
                ChronoUnit.HOURS,
                TimeSlot.NIGHT,
                new HashSet<>(Arrays.asList(TimeSlot.values()))
        );
        constraintConsecutiveShifts.setViolable(true);

        //Constraint constraint1 = new ConstraintCategorieUtenteTurno();
        Constraint constraint2 = new ConstraintMaxPeriodoConsecutivo(configVincoli.getNumMaxMinutiConsecutiviPerTutti());
        Constraint constraint4 = new ConstraintMaxOrePeriodo(configVincoli.getNumGiorniPeriodo(), configVincoli.getMaxMinutiPeriodo());
        Constraint constraint5 = new ConstraintUbiquità();
        Constraint constraint6 = new ConstraintNumeroDiRuoloTurno();
        Constraint constraint7 = new ConstraintHoliday();

        //constraint1.setViolable(true);
        constraint7.setViolable(true);

        for (ConfigVincMaxPerCons config : configVincoli.getConfigVincMaxPerConsPerCategoria()) {
            Constraint vincolo = new ConstraintMaxPeriodoConsecutivo(config.getNumMaxMinutiConsecutivi(), config.getCategoriaVincolata());
            vincolo.setDescription("Constraint massimo periodo consecutivo per categoria " + config.getCategoriaVincolata().getType());
            constraintDAO.saveAndFlush(vincolo);
        }
        //constraint1.setDescription("Constraint Shift Persona: verifica che una determinata categoria non venga associata ad un turno proibito.");
        constraint2.setDescription("Constraint massimo periodo consecutivo. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        constraint4.setDescription("Constraint massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        constraint5.setDescription("Constraint ubiquità. Verifica che lo stesso medico non venga assegnato contemporaneamente a due turni diversi nello stesso giorno");
        constraintConsecutiveShifts.setDescription("Constraint turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");
        constraint6.setDescription("Constraint numero utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno");
        constraint7.setDescription("Vincolo festività. Verifica che un medico che l'anno precedente ha lavorato durante una certa festività non venga assegnato a un turno corrispondente alla medesima festività.");

        constraintDAO.saveAndFlush(constraintConsecutiveShifts);
        //constraintDAO.saveAndFlush(constraint1);
        constraintDAO.saveAndFlush(constraint2);
        constraintDAO.saveAndFlush(constraint4);
        constraintDAO.saveAndFlush(constraint5);
        constraintDAO.saveAndFlush(constraint6);
        constraintDAO.saveAndFlush(constraint7);

        //List<Constraint> constraints = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");

    }


    private void populateDB() throws ShiftException {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        // Condition may be structure specific TODO: Ask if it is needed a configuration file for that
        PermanentCondition over62 = new PermanentCondition("OVER_62");
        TemporaryCondition pregnant = new TemporaryCondition("INCINTA", LocalDate.now().toEpochDay(), LocalDate.now().plusMonths(9).toEpochDay());
        TemporaryCondition maternity = new TemporaryCondition("IN_MATERNITA'", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(60).toEpochDay());
        TemporaryCondition vacation = new TemporaryCondition("IN_FERIE", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());
        TemporaryCondition sick = new TemporaryCondition("IN_MALATTIA", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());


        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE (INCLUSIVE)
        Specialization cardiologia = new Specialization("CARDIOLOGIA");
        Specialization oncologia = new Specialization("ONCOLOGIA");

        Task ward = new Task(TaskEnum.WARD);
        Task clinic = new Task(TaskEnum.CLINIC);
        Task emergency = new Task(TaskEnum.EMERGENCY);
        Task operatingRoom = new Task(TaskEnum.OPERATING_ROOM);


        // Save in persistence all possible conditions
        temporaryConditionDAO.save(vacation);
        temporaryConditionDAO.save(pregnant);
        temporaryConditionDAO.save(sick);
        temporaryConditionDAO.save(maternity);

        permanentConditionDAO.save(over62);

        // Save in persistence all possible specialization
        specializationDAO.save(cardiologia);
        specializationDAO.save(oncologia);

        taskDAO.save(clinic);
        taskDAO.save(emergency);
        taskDAO.save(operatingRoom);
        taskDAO.save(ward);

        // Load services offered by the wards
        // Save in persistence all possible rotations
        MedicalService repartoCardiologia = medicalServiceController.createService(Collections.singletonList(ward), "CARDIOLOGIA");
        MedicalService ambulatorioCardiologia = medicalServiceController.createService(Collections.singletonList(clinic), "CARDIOLOGIA");
        MedicalService guardiaCardiologia = medicalServiceController.createService(Collections.singletonList(emergency), "CARDIOLOGIA");
        MedicalService salaOperatoriaCardiologia = medicalServiceController.createService(Collections.singletonList(operatingRoom), "CARDIOLOGIA");
        MedicalService ambulatorioOncologia = medicalServiceController.createService(Collections.singletonList(clinic), "ONCOLOGIA");

        //Creo utenti
        UserController userController = new UserController();

        Doctor u6 = new Doctor("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.PLANNER));
        try {
            userController.addCondition(u6, over62);
            userController.addCondition(u6, vacation);
            userController.addSpecialization(u6, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Doctor u1 = new Doctor("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.CONFIGURATOR));
        try {
            userController.addCondition(u6, over62);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Doctor u2 = new Doctor("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u3 = new Doctor("Federica", "Villani", "VLNFDR98P43H501D", LocalDate.of(1998, 9, 3), "federicavillani@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u4 = new Doctor("Daniele", "Colavecchi", "CLVDNL82C21H501E", LocalDate.of(1982, 7, 6), "danielecolavecchi@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u4, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u5 = new Doctor("Daniele", "La Prova", "LPVDNL98R27H501J", LocalDate.of(1998, 2, 12), "danielelaprova@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u5, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u7 = new Doctor("Luca", "Fiscariello", "FSCLCU98L07B581O", LocalDate.of(1998, 8, 12), "lucafiscariello@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u7, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u8 = new Doctor("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u8, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u9 = new Doctor("Giulia", "Cantone", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u10 = new Doctor("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u11 = new Doctor("Giada", "Rossi", "RSSGDI92H68H501O", LocalDate.of(1997, 3, 14), "giada.rossi@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u12 = new Doctor("Camilla", "Verdi", "VRDCML95B41H501L", LocalDate.of(1997, 5, 23), "camilla.verdi@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u13 = new Doctor("Federica", "Pollini", "PLLFDR94S70H501I", LocalDate.of(1998, 2, 12), "federica.pollini@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u14 = new Doctor("Claudia", "Rossi", "RSSCLD91C52H501A", LocalDate.of(1982, 7, 6), "claudia.rossi@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u14, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u15 = new Doctor("Giorgio", "Bianchi", "BNCGRG88E21H501S", LocalDate.of(1993, 2, 12), "giorgio.bianchi@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u15, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u16 = new Doctor("Claudio", "Gialli", "GLLCLD89B14H501T", LocalDate.of(1998, 8, 12), "claudia.gialli@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u16, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u17 = new Doctor("Filippo", "Neri", "NREFLP92R24H501C", LocalDate.of(1998, 2, 12), "filippo.neru@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u18 = new Doctor("Vincenzo", "Grassi", "GRSVNC60A19H501P", LocalDate.of(1998, 8, 12), "vincenzo.grassi@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u18, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u19 = new Doctor("Diana", "Pasquali", "PSQDNI97D22H501Q", LocalDate.of(1997, 4, 22), "diana.pasquali@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u20 = new Doctor("Francesco", "Lo Presti", "LPSFRC66T05G071E", LocalDate.of(1998, 8, 12), "francesco.lopresti@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        Doctor u21 = new Doctor("Andrea", "Pepe", "PPENDR99M05I150J", LocalDate.of(1999, 8, 5), "andrea.pepe@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u22 = new Doctor("Matteo", "Fanfarillo", "FNFMTT99E10A123E", LocalDate.of(1999, 5, 10), "matteo.fanfarillo99@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.PLANNER));
        Doctor u23 = new Doctor("Matteo", "Ciccaglione", "CCCMTT99H15C439X", LocalDate.of(1998, 6, 15), "matteo.ciccaglione@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u24 = new Doctor("Vittoria", "De Nitto", "DNTVTT60C59E612D", LocalDate.of(1998, 8, 12), "vittoria.denitto@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u24, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u25 = new Doctor("Valeria", "Cardellini", "CRDVLR68L44H501B", LocalDate.of(1998, 8, 12), "valeria.cardellini@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u25, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u26 = new Doctor("Roberto", "Monte", "MNTRBT69R01D666W", LocalDate.of(1998, 8, 12), "roberto.monte@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u26, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u27 = new Doctor("Giovanni", "Saggio", "SGGGVN65D30H501J", LocalDate.of(1998, 8, 12), "giovanni.saggio@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u27, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u28 = new Doctor("Livia", "Simoncini", "SMNLVI98L17H501O", LocalDate.of(1998, 7, 19), "livia.simoncini@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u29 = new Doctor("Ludovico", "Zarrelli", "ZRRLDV99E03I370A", LocalDate.of(1998, 5, 3), "ludovico.zerrelli@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u30 = new Doctor("Alessandro", "Montenegro", "MNTLSS96P20H501J", LocalDate.of(1998, 8, 12), "alessandro.montenegro@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u31 = new Doctor("Daniel", "Lungu", "LNGDNL98T04H501I", LocalDate.of(1998, 12, 4), "daniel.lungu@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u32 = new Doctor("Andrea", "Tosti", "TSTNDR97A10H501E", LocalDate.of(1998, 8, 12), "andrea.tosti@gmail.com", "passw", Seniority.SPECIALIST_JUNIOR, List.of(SystemActor.DOCTOR));
        Doctor u33 = new Doctor("Giorgio", "Pesce", "PSCGRG98E08H501T", LocalDate.of(1998, 8, 12), "giorgia.pesce@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));
        Doctor u34 = new Doctor("Valerio", "Palmerini", "PLMVLR93B12H501U", LocalDate.of(1998, 8, 12), "valerio.palmerini@gmail.com", "passw", Seniority.SPECIALIST_SENIOR, List.of(SystemActor.DOCTOR));

        u6 = doctorDAO.saveAndFlush(u6);
        u7 = doctorDAO.saveAndFlush(u7);
        u1 = doctorDAO.saveAndFlush(u1);
        u2 = doctorDAO.saveAndFlush(u2);
        u3 = doctorDAO.saveAndFlush(u3);
        u4 = doctorDAO.saveAndFlush(u4);
        u5 = doctorDAO.saveAndFlush(u5);
        u8 = doctorDAO.saveAndFlush(u8);
        u9 = doctorDAO.saveAndFlush(u9);
        u10 = doctorDAO.saveAndFlush(u10);
        u11 = doctorDAO.saveAndFlush(u11);
        u12 = doctorDAO.saveAndFlush(u12);
        u13 = doctorDAO.saveAndFlush(u13);
        u14 = doctorDAO.saveAndFlush(u14);
        u15 = doctorDAO.saveAndFlush(u15);
        u16 = doctorDAO.saveAndFlush(u16);
        u17 = doctorDAO.saveAndFlush(u17);
        u18 = doctorDAO.saveAndFlush(u18);
        u19 = doctorDAO.saveAndFlush(u19);
        u20 = doctorDAO.saveAndFlush(u20);
        u21 = doctorDAO.saveAndFlush(u21);
        u22 = doctorDAO.saveAndFlush(u22);
        u23 = doctorDAO.saveAndFlush(u23);
        u24 = doctorDAO.saveAndFlush(u24);
        u25 = doctorDAO.saveAndFlush(u25);
        u26 = doctorDAO.saveAndFlush(u26);
        u27 = doctorDAO.saveAndFlush(u27);
        u28 = doctorDAO.saveAndFlush(u28);
        u29 = doctorDAO.saveAndFlush(u29);
        u30 = doctorDAO.saveAndFlush(u30);
        u31 = doctorDAO.saveAndFlush(u31);
        u32 = doctorDAO.saveAndFlush(u32);
        u33 = doctorDAO.saveAndFlush(u33);
        u34 = doctorDAO.saveAndFlush(u34);

        HashMap<Seniority, Integer> doctorsNumberBySeniority = new HashMap<>();
        doctorsNumberBySeniority.put(Seniority.STRUCTURED, 1);
        doctorsNumberBySeniority.put(Seniority.SPECIALIST_SENIOR, 1);
        doctorsNumberBySeniority.put(Seniority.SPECIALIST_JUNIOR, 1);


        Set<DayOfWeek> allDaysOfWeek = new HashSet<>();
        allDaysOfWeek.add(DayOfWeek.MONDAY);
        allDaysOfWeek.add(DayOfWeek.TUESDAY);
        allDaysOfWeek.add(DayOfWeek.WEDNESDAY);
        allDaysOfWeek.add(DayOfWeek.THURSDAY);
        allDaysOfWeek.add(DayOfWeek.FRIDAY);
        allDaysOfWeek.add(DayOfWeek.SATURDAY);
        allDaysOfWeek.add(DayOfWeek.SUNDAY);

        Shift shift1 = new Shift(LocalTime.of(14, 0),
                Duration.ofHours(8),
                ambulatorioCardiologia,
                TimeSlot.MORNING,
                doctorsNumberBySeniority,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift1);

        Shift shift2 = new Shift(LocalTime.of(9, 0),
                Duration.ofHours(8),
                ambulatorioCardiologia,
                TimeSlot.NIGHT,
                doctorsNumberBySeniority,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift2);

        Shift shift3 = new Shift(LocalTime.of(0, 0),
                Duration.ofHours(8),
                ambulatorioCardiologia,
                TimeSlot.AFTERNOON,
                doctorsNumberBySeniority,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift3);


        //creation of the DoctorHolidays instances
        List<Doctor> allDoctors = doctorDAO.findAll();
        registerHolidays();
        List<Holiday> holidays = holidayDAO.findAll();  //retrieve of holiday entities (and not DTOs)

        for(Doctor doctor: allDoctors) {
            DoctorUffaPriority dup = new DoctorUffaPriority(doctor);
            DoctorHolidays dh = new DoctorHolidays(doctor, holidays);

            doctorUffaPriorityDAO.save(dup);
            //doctorHolidaysDAO.save(dh);   //TODO: DEBUG PLZ

        }

        //TODO: Eliminare in seguito
        List<ConcreteShift> lc= new ArrayList<>();
        ConcreteShift concreteShift1 = new ConcreteShift(LocalDate.now().toEpochDay(),shift1);
        concreteShift1=concreteShiftDAO.saveAndFlush(concreteShift1);
        lc.add(concreteShift1);
        ConcreteShift concreteShift2 = new ConcreteShift(LocalDate.now().plusDays(1).toEpochDay(),shift1);
        concreteShift2=concreteShiftDAO.saveAndFlush(concreteShift2);
        lc.add(concreteShift2);
        ConcreteShift concreteShift3 = new ConcreteShift(LocalDate.now().plusDays(2).toEpochDay(),shift1);
        concreteShift3=concreteShiftDAO.saveAndFlush(concreteShift3);
        lc.add(concreteShift3);
        ConcreteShift concreteShift4 = new ConcreteShift(LocalDate.now().toEpochDay(), shift1);
        concreteShift4 = concreteShiftDAO.saveAndFlush(concreteShift4);
        lc.add(concreteShift4);


        DoctorAssignment da1 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_CALL,concreteShift1,ward);
        DoctorAssignment da7 = new DoctorAssignment(u13, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift1, ward);
        DoctorAssignment da8 = new DoctorAssignment(u30, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift1, ward);
        DoctorAssignment da9 = new DoctorAssignment(u33, ConcreteShiftDoctorStatus.ON_CALL, concreteShift1, ward);
        doctorAssignmentDAO.saveAndFlush(da1);
        doctorAssignmentDAO.saveAndFlush(da7);
        doctorAssignmentDAO.saveAndFlush(da8);
        doctorAssignmentDAO.saveAndFlush(da9);
        concreteShift1.getDoctorAssignmentList().add(da1);
        concreteShift1.getDoctorAssignmentList().add(da7);
        concreteShift1.getDoctorAssignmentList().add(da8);
        concreteShift1.getDoctorAssignmentList().add(da9);
        concreteShiftDAO.saveAndFlush(concreteShift1);

        DoctorAssignment da2 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_DUTY,concreteShift2,ward);
        doctorAssignmentDAO.saveAndFlush(da2);
        concreteShift2.getDoctorAssignmentList().add(da2);
        DoctorAssignment da4 = new DoctorAssignment(u4, ConcreteShiftDoctorStatus.ON_CALL, concreteShift2, ward);
        doctorAssignmentDAO.saveAndFlush(da4);
        concreteShift2.getDoctorAssignmentList().add(da4);
        DoctorAssignment da5 = new DoctorAssignment(u9, ConcreteShiftDoctorStatus.ON_CALL, concreteShift2, ward);
        doctorAssignmentDAO.saveAndFlush(da5);
        concreteShift2.getDoctorAssignmentList().add(da5);
        DoctorAssignment da6 = new DoctorAssignment(u23, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift2, ward);
        doctorAssignmentDAO.saveAndFlush(da6);
        concreteShift2.getDoctorAssignmentList().add(da6);

        concreteShiftDAO.saveAndFlush(concreteShift2);

        DoctorAssignment da3 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_DUTY,concreteShift3,ward);
        DoctorAssignment da10 = new DoctorAssignment(u2, ConcreteShiftDoctorStatus.ON_DUTY,concreteShift3,ward);
        DoctorAssignment da11 = new DoctorAssignment(u20, ConcreteShiftDoctorStatus.ON_CALL,concreteShift3,ward);
        DoctorAssignment da12 = new DoctorAssignment(u29, ConcreteShiftDoctorStatus.ON_CALL,concreteShift3,ward);
        doctorAssignmentDAO.saveAndFlush(da3);
        doctorAssignmentDAO.saveAndFlush(da10);
        doctorAssignmentDAO.saveAndFlush(da11);
        doctorAssignmentDAO.saveAndFlush(da12);
        concreteShift3.getDoctorAssignmentList().add(da3);
        concreteShift3.getDoctorAssignmentList().add(da10);
        concreteShift3.getDoctorAssignmentList().add(da11);
        concreteShift3.getDoctorAssignmentList().add(da12);
        concreteShiftDAO.saveAndFlush(concreteShift3);

        DoctorAssignment da13 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift4, ward);
        DoctorAssignment da14 = new DoctorAssignment(u9, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift4, ward);
        DoctorAssignment da15 = new DoctorAssignment(u7, ConcreteShiftDoctorStatus.ON_DUTY, concreteShift4, ward);
        DoctorAssignment da16 = new DoctorAssignment(u10, ConcreteShiftDoctorStatus.ON_CALL, concreteShift4, ward);
        doctorAssignmentDAO.saveAndFlush(da13);
        doctorAssignmentDAO.saveAndFlush(da14);
        doctorAssignmentDAO.saveAndFlush(da15);
        doctorAssignmentDAO.saveAndFlush(da16);
        concreteShift4.getDoctorAssignmentList().add(da13);
        concreteShift4.getDoctorAssignmentList().add(da14);
        concreteShift4.getDoctorAssignmentList().add(da15);
        concreteShift4.getDoctorAssignmentList().add(da16);
        concreteShiftDAO.saveAndFlush(concreteShift4);


        List<Doctor> ld = new ArrayList<Doctor>();
        ld.add(u2);
        ld.add(u3);
        ld.add(u4);
        ld.add(u5);
        ld.add(u7);
        ld.add(u8);
        ld.add(u9);
        ld.add(u10);
        ld.add(u11);
        ld.add(u12);
        ld.add(u13);
        ld.add(u14);
        ld.add(u15);
        ld.add(u16);
        ld.add(u17);
        ld.add(u18);
        ld.add(u19);
        ld.add(u20);
        ld.add(u21);
        ld.add(u23);
        ld.add(u24);
        ld.add(u25);
        ld.add(u26);
        ld.add(u27);
        ld.add(u28);
        ld.add(u29);
        ld.add(u30);
        ld.add(u31);
        ld.add(u32);
        ld.add(u33);
        ld.add(u34);
        List<Constraint> vincoli = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");
        Schedule s= null;
        List<Constraint> v= new ArrayList<>();
        try {
            s = new ScheduleBuilder(
                    LocalDate.now(),
                    LocalDate.now().plusDays(7),
                    v,
                    lc,
                    ld,
                    holidays,
                    doctorHolidaysDAO.findAll(),
                    doctorUffaPriorityDAO.findAll()
            ).build();
        } catch (IllegalScheduleException e) {
            throw new RuntimeException(e);
        }



        //scheduleDAO.save(s);
        /*
        ScheduleRestEndpoint restSchedule=new ScheduleRestEndpoint();
        ScheduleGenerationDTO gs=new ScheduleGenerationDTO();
        gs.setInitialDay(15);
        gs.setInitialMonth(1);
        gs.setInitialYear(2024);
        gs.setFinalDay(30);
        gs.setFinalMonth(1);
        gs.setFinalYear(2024);
        restSchedule.createSchedule(gs);
         */
        /*




        Shift t2 = new Shift(LocalTime.of(14, 0), Duration.ofHours(6), Collections.singletonList(repartoCardiologia1), TimeSlot.AFTERNOON, qssList, allDaysOfWeek, Collections.emptyList());

        t2.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(null,sick, t2, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(null,vacation, t2,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t2.setRotationPolicies(List.of(
                new RotationPolicy(repartoCardiologia, t2, UserCategoryPolicyValue.INCLUDE)
        ));



        Shift t3 = new Shift(LocalTime.of(20, 0), Duration.ofHours(12), Collections.singletonList(repartoCardiologia2), TimeSlot.NIGHT, qssList, allDaysOfWeek, Collections.emptyList());

        t3.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(null,sick, t3, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(null,vacation, t3,  UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(null,pregnant, t3,  UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(over62, t3,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t3.setRotationPolicies(List.of(
                new RotationPolicy(repartoCardiologia, t3, UserCategoryPolicyValue.INCLUDE)
        ));



        Shift t5 = new Shift(LocalTime.of(10, 0), Duration.ofHours(2), Collections.singletonList(ambulatorioCardiologia), TimeSlot.MORNING, qssList, allDaysOfWeek, Collections.emptyList());

        t5.setConditionPolicies(Arrays.asList(
            new ConditionPolicy(null,sick, t5, UserCategoryPolicyValue.EXCLUDE),
            new ConditionPolicy(null,vacation, t5,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t5.setSpecializationPolicies(List.of(
                new SpecializationPolicy(cardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));

        t5.setRotationPolicies(List.of(
                new RotationPolicy(ambulatorioCardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));



        Shift t6 = new Shift(LocalTime.of(10, 0), Duration.ofHours(2), Collections.singletonList(ambulatorioOncologia), TimeSlot.MORNING, qssList, allDaysOfWeek, Collections.emptyList());

        t6.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(null,sick, t6, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(null,vacation, t6,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t6.setSpecializationPolicies(List.of(
                new SpecializationPolicy(oncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));

        t6.setRotationPolicies(List.of(
                new RotationPolicy(ambulatorioOncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));



        // Creazione del turno in sala operatoria in cardiologia ogni lunedì
        Shift salaOpCardio = new Shift(LocalTime.of(10, 0), Duration.ofHours(13).plusMinutes(59), Collections.singletonList(salaOperatoriaCardiologia), TimeSlot.MORNING, qssList, Collections.singletonList(DayOfWeek.MONDAY), Collections.emptyList());

        //Salvataggio dei Turni nel DB
        turnoDao.saveAndFlush(t1);
        turnoDao.saveAndFlush(t2);
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);
        turnoDao.saveAndFlush(t6);
        turnoDao.saveAndFlush(salaOpCardio);

        Preference preference = new Preference(LocalDate.of(2023,3,12),new ArrayList<>(), Collections.singletonList(u3));
        u3.getPreferenceList().add(preference);

        desiderataDao.save(preference);
        doctorDao.saveAndFlush(u3);

*/
    }


    /**
     * Metodo che server per caricare le festività dell'anno 2023/2024
     */
    public void LoadHoliday() throws IOException {
        List<List<String>> data = new ArrayList<>();
        //String filePath = currPath+"\\src\\main\\resources\\holiday.csv";
        String filePath = "";
        File file = new File("src/main/resources/holiday.csv");
        filePath = file.getAbsolutePath();

        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while (line != null) {
            List<String> lineData = Arrays.asList(line.split(";"));//splitting lines
            data.add(lineData);
            line = br.readLine();
        }
        for (List<String> list : data) {
            String HolidayData = Arrays.asList(list.get(0).split(";")).get(0);
            final String[] HolidayDataS = HolidayData.split("/");
            int year = Integer.parseInt(HolidayDataS[2].replaceAll("[^0-9]", ""));
            int month = Integer.parseInt(HolidayDataS[1].replaceAll("[^0-9]", ""));
            int day = Integer.parseInt(HolidayDataS[0].replaceAll("[^0-9]", ""));
            String HolidayName = Arrays.asList(list.get(1).split(";")).get(0);
            String HolidayLocation = Arrays.asList(list.get(2).split(";")).get(0);
            String Holiday_Category = Arrays.asList(list.get(3).split(";")).get(0);
            LocalDate Date = LocalDate.of(year, month, day);
            /*holidayController.registerHolidayPeriod(new HolidayDTO(
                    HolidayName,
                    HolidayCategory.valueOf(Holiday_Category),
                    Date.toEpochDay(),
                    Date.toEpochDay(),
                    HolidayLocation
            ));*/
        }
        br.close();
    }
}
