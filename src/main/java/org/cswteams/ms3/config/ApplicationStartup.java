package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.aspectj.weaver.loadtime.definition.Definition;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.scheduler.ScheduleBuilder;
import org.cswteams.ms3.control.task.TaskController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.condition.*;
import org.cswteams.ms3.entity.constraint.*;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.entity.scocciature.ScocciaturaAssegnazioneUtente;
import org.cswteams.ms3.entity.scocciature.ScocciaturaDesiderata;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.IllegalScheduleException;
import org.cswteams.ms3.exception.ShiftException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


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
    private DoctorDAO doctorDAO;

    @Autowired
    private ShiftDAO shiftDAO;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private DoctorAssignmentDAO doctorAssignmentDAO;

    @Autowired
    private MedicalServiceDAO medicalServiceDAO;

    @Autowired
    private PermanentConditionDAO permanentConditionDAO;

    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO;

    @Autowired
    private SpecializationDAO specializationDAO;

    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ConstraintDAO constraintDAO;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private ScocciaturaDAO scocciaturaDAO;
  
    @Autowired
    private PreferenceDAO preferenceDao;

    @Autowired
    private ConfigVincoliDAO configVincoliDAO;

    @Autowired
    private ConfigVincoloMaxPeriodoConsecutivoDAO configVincoloMaxPeriodoConsecutivoDAO;

    @Autowired
    private ScheduleDAO scheduleDAO;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
        //if (doctorDAO.count() == 0) {
            try {
                populateDB();
            } catch (ShiftException e) {
                e.printStackTrace();
            }

            //registerConstraints();
            registerScocciature();
        //}

    }

    private void registerScocciature() {
        int pesoDesiderata = 100;

        int pesoDomenicaPomeriggio = 20;
        int pesoDomenicaMattina = 20;
        int pesoSabatoNotte = 20;

        int pesoSabatoPomeriggio = 15;
        int pesoSabatoMattina = 15;
        int pesoVenerdiNotte = 15;
        int pesoDomenicaNotte = 15;

        int pesoVenerdiPomeriggio = 10;

        int pesoFerialeSemplice = 5;
        int pesoFerialeNotturno = 10;

        Scocciatura scocciaturaDomenicaMattina = new ScocciaturaAssegnazioneUtente(pesoDomenicaMattina, DayOfWeek.SUNDAY, TimeSlot.MORNING);
        Scocciatura scocciaturaDomenicaPomeriggio = new ScocciaturaAssegnazioneUtente(pesoDomenicaPomeriggio, DayOfWeek.SUNDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaDomenicaNotte = new ScocciaturaAssegnazioneUtente(pesoDomenicaNotte, DayOfWeek.SUNDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaSabatoMattina = new ScocciaturaAssegnazioneUtente(pesoSabatoMattina, DayOfWeek.SATURDAY, TimeSlot.MORNING);
        Scocciatura scocciaturaSabatoPomeriggio = new ScocciaturaAssegnazioneUtente(pesoSabatoPomeriggio, DayOfWeek.SATURDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaSabatoNotte = new ScocciaturaAssegnazioneUtente(pesoSabatoNotte, DayOfWeek.SATURDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaVenerdiPomeriggio = new ScocciaturaAssegnazioneUtente(pesoVenerdiPomeriggio, DayOfWeek.FRIDAY, TimeSlot.AFTERNOON);
        Scocciatura scocciaturaVenerdiNotte = new ScocciaturaAssegnazioneUtente(pesoVenerdiNotte, DayOfWeek.FRIDAY, TimeSlot.NIGHT);

        Scocciatura scocciaturaDesiderata = new ScocciaturaDesiderata(pesoDesiderata);

        scocciaturaDAO.save(scocciaturaDomenicaPomeriggio);
        scocciaturaDAO.save(scocciaturaDomenicaMattina);
        scocciaturaDAO.save(scocciaturaDomenicaNotte);

        scocciaturaDAO.save(scocciaturaSabatoMattina);
        scocciaturaDAO.save(scocciaturaSabatoPomeriggio);
        scocciaturaDAO.save(scocciaturaSabatoNotte);

        scocciaturaDAO.save(scocciaturaVenerdiPomeriggio);
        scocciaturaDAO.save(scocciaturaVenerdiNotte);

        scocciaturaDAO.save(scocciaturaDesiderata);

        List<DayOfWeek> giorniFeriali = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        for (DayOfWeek giornoFeriale : giorniFeriali) {
            ScocciaturaAssegnazioneUtente scocciaturaFerialeMattina = new ScocciaturaAssegnazioneUtente(pesoFerialeSemplice, giornoFeriale, TimeSlot.MORNING);
            scocciaturaDAO.save(scocciaturaFerialeMattina);
            if (giornoFeriale != DayOfWeek.FRIDAY) {
                ScocciaturaAssegnazioneUtente scocciaturaFerialePomeriggio = new ScocciaturaAssegnazioneUtente(pesoFerialeSemplice, giornoFeriale, TimeSlot.AFTERNOON);
                scocciaturaDAO.save(scocciaturaFerialePomeriggio);
                ScocciaturaAssegnazioneUtente scocciaturaFerialeNotturno = new ScocciaturaAssegnazioneUtente(pesoFerialeNotturno, giornoFeriale, TimeSlot.NIGHT);
                scocciaturaDAO.save(scocciaturaFerialeNotturno);
            }
        }
    }

    private void registerConstraints(){

        ConfigVincoli configVincoli;
        try {
            File file = new File("src/main/resources/configVincoliDefault.properties");
            FileInputStream propsInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(propsInput);

            ConfigVincMaxPerCons confOver62 = new ConfigVincMaxPerCons(permanentConditionDAO.findByType("OVER_62"),Integer.parseInt(prop.getProperty("numMaxOreConsecutiveOver62"))*60);
            ConfigVincMaxPerCons confIncinta = new ConfigVincMaxPerCons(temporaryConditionDAO.findByType("INCINTA"),Integer.parseInt(prop.getProperty("numMaxOreConsecutiveDonneIncinta"))*60);
            configVincoloMaxPeriodoConsecutivoDAO.saveAndFlush(confOver62);
            configVincoloMaxPeriodoConsecutivoDAO.saveAndFlush(confIncinta);
            configVincoli = new ConfigVincoli(
                    Integer.parseInt(prop.getProperty("numGiorniPeriodo")),
                    Integer.parseInt(prop.getProperty("maxOrePeriodo")) * 60,
                    Integer.parseInt(prop.getProperty("HorizonTurnoNotturno")),
                    Integer.parseInt(prop.getProperty("numMaxOreConsecutivePerTutti")) * 60,
                    Arrays.asList(confOver62,confIncinta)
            );
            configVincoliDAO.save(configVincoli);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // nessun turno può essere allocato a questa persona durante il suo smonto notte
        ConstraintTipologieTurniContigue vincoloTurniContigui = new ConstraintTipologieTurniContigue(
            configVincoli.getHorizonTurnoNotturno(),
            ChronoUnit.HOURS,
            TimeSlot.NIGHT,
            new HashSet<>(Arrays.asList(TimeSlot.values()))
        );
        vincoloTurniContigui.setViolable(true);

        //Constraint vincolo1 = new ConstraintCategorieUtenteTurno();
        Constraint vincolo2 = new ConstraintMaxPeriodoConsecutivo(configVincoli.getNumMaxMinutiConsecutiviPerTutti());
        Constraint vincolo4 = new ConstraintMaxOrePeriodo(configVincoli.getNumGiorniPeriodo(), configVincoli.getMaxMinutiPeriodo());
        Constraint vincolo5 = new ConstraintUbiquità();
        Constraint vincolo6 = new ConstraintNumeroDiRuoloTurno();

        //vincolo1.setViolable(true);

        for(ConfigVincMaxPerCons config : configVincoli.getConfigVincMaxPerConsPerCategoria()){
            Constraint vincolo = new ConstraintMaxPeriodoConsecutivo(config.getNumMaxMinutiConsecutivi(), config.getCategoriaVincolata());
            vincolo.setDescription("Constraint massimo periodo consecutivo per categoria "+config.getCategoriaVincolata().getType());
            constraintDAO.saveAndFlush(vincolo);
        }
        //vincolo1.setDescription("Constraint Shift Persona: verifica che una determinata categoria non venga associata ad un turno proibito.");
        vincolo2.setDescription("Constraint massimo periodo consecutivo. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        vincolo4.setDescription("Constraint massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        vincolo5.setDescription("Constraint ubiquità. Verifica che lo stesso medico non venga assegnato contemporaneamente a due turni diversi nello stesso giorno");
        vincoloTurniContigui.setDescription("Constraint turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");
        vincolo6.setDescription("Constraint numero utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno");

        constraintDAO.saveAndFlush(vincoloTurniContigui);
        //constraintDAO.saveAndFlush(vincolo1);
        constraintDAO.saveAndFlush(vincolo2);
        constraintDAO.saveAndFlush(vincolo4);
        constraintDAO.saveAndFlush(vincolo5);
        constraintDAO.saveAndFlush(vincolo6);

        List<Constraint> vincoli = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");
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


        // Load services offered by the wards
        MedicalService repartoCardiologia = new MedicalService(Collections.singletonList(ward), "CARDIOLOGIA");
        MedicalService ambulatorioCardiologia = new MedicalService(Collections.singletonList(clinic), "CARDIOLOGIA");
        MedicalService guardiaCardiologia = new MedicalService(Collections.singletonList(emergency), "CARDIOLOGIA");
        MedicalService salaOperatoriaCardiologia = new MedicalService(Collections.singletonList(operatingRoom), "CARDIOLOGIA");
        MedicalService ambulatorioOncologia = new MedicalService(Collections.singletonList(clinic), "ONCOLOGIA");

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


        // Save in persistence all possible rotations
        medicalServiceDAO.save(repartoCardiologia);
        medicalServiceDAO.save(ambulatorioCardiologia);
        medicalServiceDAO.save(guardiaCardiologia);
        medicalServiceDAO.save(salaOperatoriaCardiologia);
        medicalServiceDAO.save(ambulatorioOncologia);


        //Creo utenti
        UserController userController = new UserController();
        TaskController taskController = new TaskController();

        Doctor u6 = new Doctor("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.PLANNER));
        try {
            userController.addCondition(u6, over62);
            userController.addCondition(u6, vacation);
            userController.addSpecialization(u6, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Doctor u1 = new Doctor("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.CONFIGURATOR));
        try {
            userController.addCondition(u6, over62);
            taskController.addService(repartoCardiologia, u6);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        Doctor u2 = new Doctor("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(repartoCardiologia, u2);
        Doctor u3 = new Doctor("Federica", "Villani", "VLNFDR98P43H501D", LocalDate.of(1998, 9, 3), "federicavillani@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(repartoCardiologia, u3);
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
        Doctor u8 = new Doctor("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u8, cardiologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u9 = new Doctor("Giulia", "Cantone", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(repartoCardiologia, u9);
        Doctor u10 = new Doctor("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(repartoCardiologia, u10);
        Doctor u11 = new Doctor("Giada", "Rossi", "RSSGDI92H68H501O", LocalDate.of(1997, 3, 14), "giada.rossi@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioOncologia, u11);
        Doctor u12 = new Doctor("Camilla", "Verdi", "VRDCML95B41H501L", LocalDate.of(1997, 5, 23), "camilla.verdi@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioCardiologia, u12);
        Doctor u13 = new Doctor("Federica", "Pollini", "PLLFDR94S70H501I", LocalDate.of(1998, 2, 12), "federica.pollini@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioCardiologia, u13);
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
        Doctor u17 = new Doctor("Filippo", "Neri", "NREFLP92R24H501C", LocalDate.of(1998, 2, 12), "filippo.neru@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioCardiologia, u17);
        Doctor u18 = new Doctor("Vincenzo", "Grassi", "GRSVNC60A19H501P", LocalDate.of(1998, 8, 12), "vincenzo.grassi@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        try {
            userController.addSpecialization(u18, oncologia);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Doctor u19 = new Doctor("Diana", "Pasquali", "PSQDNI97D22H501Q", LocalDate.of(1997, 4, 22), "diana.pasquali@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioCardiologia, u19);
        Doctor u20 = new Doctor("Francesco", "Lo Presti", "LPSFRC66T05G071E", LocalDate.of(1998, 8, 12), "francesco.lopresti@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioOncologia, u20);
        Doctor u21 = new Doctor("Andrea", "Pepe", "PPENDR99M05I150J", LocalDate.of(1999, 8, 5), "andrea.pepe@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioOncologia, u21);
        Doctor u22 = new Doctor("Matteo", "Fanfarillo", "FNFMTT99E10A123E", LocalDate.of(1999, 5, 10), "matteo.fanfarillo99@gmail.com", "passw", Seniority.STRUCTURED, List.of(SystemActor.PLANNER));
        taskController.addService(ambulatorioOncologia, u22);
        Doctor u23 = new Doctor("Matteo", "Ciccaglione", "CCCMTT99H15C439X", LocalDate.of(1998, 6, 15), "matteo.ciccaglione@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(ambulatorioOncologia, u23);
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
        Doctor u28 = new Doctor("Livia", "Simoncini", "SMNLVI98L17H501O", LocalDate.of(1998, 7, 19), "livia.simoncini@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(salaOperatoriaCardiologia, u28);
        Doctor u29 = new Doctor("Ludovico", "Zarrelli", "ZRRLDV99E03I370A", LocalDate.of(1998, 5, 3), "ludovico.zerrelli@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(salaOperatoriaCardiologia, u29);
        Doctor u30 = new Doctor("Alessandro", "Montenegro", "MNTLSS96P20H501J", LocalDate.of(1998, 8, 12), "alessandro.montenegro@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(salaOperatoriaCardiologia, u30);
        Doctor u31 = new Doctor("Daniel", "Lungu", "LNGDNL98T04H501I", LocalDate.of(1998, 12, 4), "daniel.lungu@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(salaOperatoriaCardiologia, u31);
        Doctor u32 = new Doctor("Andrea", "Tosti", "TSTNDR97A10H501E", LocalDate.of(1998, 8, 12), "andrea.tosti@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(guardiaCardiologia, u32);
        Doctor u33 = new Doctor("Giorgio", "Pesce", "PSCGRG98E08H501T", LocalDate.of(1998, 8, 12), "giorgia.pesce@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(guardiaCardiologia, u33);
        Doctor u34 = new Doctor("Valerio", "Palmerini", "PLMVLR93B12H501U", LocalDate.of(1998, 8, 12), "valerio.palmerini@gmail.com", "passw", Seniority.SPECIALIST, List.of(SystemActor.DOCTOR));
        taskController.addService(guardiaCardiologia, u34);

        u1 = doctorDAO.saveAndFlush(u1);
        u2 = doctorDAO.saveAndFlush(u2);
        u3 = doctorDAO.saveAndFlush(u3);
        u4 = doctorDAO.saveAndFlush(u4);
        u5 = doctorDAO.saveAndFlush(u5);
        u6 = doctorDAO.saveAndFlush(u6);
        u7 = doctorDAO.saveAndFlush(u7);
        u8 = doctorDAO.saveAndFlush(u8); // manuel mastrofini
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
        doctorsNumberBySeniority.put(Seniority.SPECIALIST, 2);

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
                TimeSlot.AFTERNOON,
                doctorsNumberBySeniority,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift1);

        //TO-DO: Eliminare in seguito
        List<ConcreteShift> lc= new ArrayList<>();
        ConcreteShift concreteShift1 = new ConcreteShift(LocalDate.of(2024, 1, 5).toEpochDay(),shift1);
        concreteShift1=concreteShiftDAO.saveAndFlush(concreteShift1);
        lc.add(concreteShift1);
        ConcreteShift concreteShift2 = new ConcreteShift(LocalDate.of(2024, 1, 6).toEpochDay(),shift1);
        concreteShift2=concreteShiftDAO.saveAndFlush(concreteShift2);
        lc.add(concreteShift2);
        ConcreteShift concreteShift3 = new ConcreteShift(LocalDate.of(2024, 1, 7).toEpochDay(),shift1);
        concreteShift3=concreteShiftDAO.saveAndFlush(concreteShift3);
        lc.add(concreteShift3);

        DoctorAssignment da1 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_CALL,concreteShift1,ward);
        doctorAssignmentDAO.saveAndFlush(da1);
        concreteShift1.getDoctorAssignmentList().add(da1);
        concreteShiftDAO.saveAndFlush(concreteShift1);

        DoctorAssignment da2 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_DUTY,concreteShift2,ward);
        doctorAssignmentDAO.saveAndFlush(da2);
        concreteShift2.getDoctorAssignmentList().add(da2);
        concreteShiftDAO.saveAndFlush(concreteShift2);

        DoctorAssignment da3 = new DoctorAssignment(u8, ConcreteShiftDoctorStatus.ON_DUTY,concreteShift3,ward);
        doctorAssignmentDAO.saveAndFlush(da3);
        concreteShift3.getDoctorAssignmentList().add(da3);
        concreteShiftDAO.saveAndFlush(concreteShift3);

        List<Doctor> ld = new ArrayList<Doctor>();
        ld.add(u8);
        ld.add(u7);
        List<Constraint> vincoli = constraintDAO.findByType("ConstraintMaxPeriodoConsecutivo");
        Schedule s= null;
        List<Constraint> v= new ArrayList<>();
        try {
            s = new ScheduleBuilder(
                    LocalDate.now(),
                    LocalDate.now().plusDays(7),
                    v,
                    lc,
                    ld
                    ).build();
        } catch (IllegalScheduleException e) {
            throw new RuntimeException(e);
        }
        //scheduleDAO.save(s);
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
