package org.cswteams.ms3.control.scheduler.constraint_tests;

import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.CalendarSettingBuilder;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.cswteams.ms3.entity.constraint.*;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.entity.scocciature.ScocciaturaAssegnazioneUtente;
import org.cswteams.ms3.entity.scocciature.ScocciaturaDesiderata;
import org.cswteams.ms3.entity.scocciature.ScocciaturaVacanza;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.enums.ServiceDataENUM;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.CalendarServiceException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ActiveProfiles("test")
public abstract class ControllerSchedulerTest {


    protected boolean isPossible ;
    protected LocalDate start ;

    protected LocalDate end ;

    @Autowired
    private ISchedulerController controller ;

    @Autowired
    private IHolidayController holidayController ;

    @Autowired
    private ICalendarServiceManager calendarServiceManager ;

    @Autowired
    private ScocciaturaDAO scocciaturaDAO ;

    @Autowired
    private HolidayDAO holidayDAO ;

    @Autowired
    private PermanentConditionDAO permanentConditionDAO ;

    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO ;

    @Autowired
    private ConfigVincoloMaxPeriodoConsecutivoDAO configVincoloMaxPeriodoConsecutivoDAO ;

    @Autowired
    private ConfigVincoliDAO configVincoliDAO ;

    @Autowired
    private ConstraintDAO constraintDAO ;

    @LocalServerPort
    private int port;

    public abstract void populateDB() ;

    @Before
    public void beforeScript() {

        System.out.println("Port :" + port);

        registerHolidays();

        try {
            //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
            // Condition may be structure specific TODO: Ask if it is needed a configuration file for that
            PermanentCondition over62 = new PermanentCondition("OVER 62");
            TemporaryCondition pregnant = new TemporaryCondition("INCINTA", LocalDate.now().toEpochDay(), LocalDate.now().plusMonths(9).toEpochDay());
            TemporaryCondition maternity = new TemporaryCondition("IN MATERNITA'", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(60).toEpochDay());
            TemporaryCondition vacation = new TemporaryCondition("IN FERIE", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());
            TemporaryCondition sick = new TemporaryCondition("IN MALATTIA", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());

            permanentConditionDAO.saveAndFlush(over62) ;
            temporaryConditionDAO.saveAndFlush(pregnant) ;
            temporaryConditionDAO.saveAndFlush(maternity) ;
            temporaryConditionDAO.saveAndFlush(vacation) ;
            temporaryConditionDAO.saveAndFlush(sick) ;

            populateDB();
        } catch (Exception e) {
            e.printStackTrace();
            fail() ;
        }

        registerConstraints();
        registerScocciature();
    }

    @Test
    @Transactional
    public void testScheduler() {

        Schedule schedule = controller.createSchedule(start, end) ;

        if(isPossible) {
            assertNull(schedule.getCauseIllegal());
        } else {
            assertNotNull(schedule.getCauseIllegal());
        }
    }
    private void registerHolidays() {
        List<HolidayDTO> holidays;

        try {
            CalendarSettingBuilder settingBuilder = new CalendarSettingBuilder(ServiceDataENUM.DATANEAGER);
            holidays = holidayController.readHolidays(new RetrieveHolidaysDTOIn(LocalDate.now().getYear(), "IT"));

            if(holidays.isEmpty()) {
                CalendarSetting setting = settingBuilder.create(String.valueOf(LocalDate.now().getYear()), "IT");
                calendarServiceManager.init(setting);
                holidays = calendarServiceManager.getHolidays();
                holidayController.registerHolidays(holidays);
            }

            //we are about to register Vigilia di Natale and Vigilia di Capodanno too. To do that, we have to retrieve Natale and Capodanno.
            for(HolidayDTO holidayDTO : holidays) {
                if(holidayDTO.getName().equals("Natale")) {
                    List<HolidayDTO> newHolidays = getNewHolidayDTOs(holidayDTO);
                    holidayController.registerHolidays(newHolidays);

                }

            }

        } catch (CalendarServiceException e) {
            e.printStackTrace();
        }

    }

    private List<HolidayDTO> getNewHolidayDTOs(HolidayDTO holidayDTO) {
        List<HolidayDTO> newHolidays = new ArrayList<>();

        HolidayDTO vigiliaDiNataleDTO = new HolidayDTO(
                "Vigilia di Natale",
                HolidayCategory.RELIGIOUS,
                holidayDTO.getStartDateEpochDay()-1,
                holidayDTO.getEndDateEpochDay()-1,
                holidayDTO.getLocation()
        );
        HolidayDTO vigiliaDiCapodannoDTO = new HolidayDTO(
                "Vigilia di Capodanno",
                HolidayCategory.CIVIL,
                holidayDTO.getStartDateEpochDay()+6,
                holidayDTO.getEndDateEpochDay()+6,
                holidayDTO.getLocation()
        );

        newHolidays.add(vigiliaDiNataleDTO);
        newHolidays.add(vigiliaDiCapodannoDTO);
        return newHolidays;
    }

    private void registerScocciature() {

        int uffaPriorityPreference;
        int uffaPriorityRespectedPreference;   //TODO: introduce priority level reduction in case of respected preference

        int uffaPrioritySundayMorning;
        int uffaPrioritySundayAfternoon;
        int uffaPrioritySaturdayNight;

        int uffaPrioritySaturdayMorning;
        int uffaPrioritySaturdayAfternoon;
        int uffaPrioritySundayNight;

        int uffaPriorityFridayAfternoon;
        int uffaPriorityFridayNight;

        int uffaPrioritySimple;
        int uffaPriorityNight;

        int uffaPriorityDefaultHoliday;
        int uffaPriorityDefaultHolidayNight;

        int uffaPriorityVigiliaDiCapodannoMorning;
        int uffaPriorityVigiliaDiCapodannoAfternoon;
        int uffaPriorityVigiliaDiCapodannoNight;
        int uffaPriorityCapodannoMorning;
        int uffaPriorityCapodannoAfternoon;
        int uffaPriorityCapodannoNight;
        int uffaPriorityPasquaMorning;
        int uffaPriorityPasquaAfternoon;
        int uffaPriorityPasquaNight;
        int uffaPriorityLunediDellAngeloMorning;
        int uffaPriorityLunediDellAngeloAfternoon;
        int uffaPriorityLunediDellAngeloNight;
        int uffaPriorityFerragostoOAssunzioneMorning;
        int uffaPriorityFerragostoOAssunzioneAfternoon;
        int uffaPriorityFerragostoOAssunzioneNight;
        int uffaPriorityVigiliaDiNataleMorning;
        int uffaPriorityVigiliaDiNataleAfternoon;
        int uffaPriorityVigiliaDiNataleNight;
        int uffaPriorityNataleMorning;
        int uffaPriorityNataleAfternoon;
        int uffaPriorityNataleNight;

        //we read uffa priorities from configuration file priority.properties
        try {
            File file = new File("src/main/resources/priority.properties");
            FileInputStream propsInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(propsInput);

            uffaPriorityPreference = Integer.parseInt(prop.getProperty("uffaPriorityPreference"));
            uffaPriorityRespectedPreference = Integer.parseInt(prop.getProperty("uffaPriorityRespectedPreference"));
            uffaPrioritySundayMorning = Integer.parseInt(prop.getProperty("uffaPrioritySundayMorning"));
            uffaPrioritySundayAfternoon = Integer.parseInt(prop.getProperty("uffaPrioritySundayAfternoon"));
            uffaPrioritySundayNight = Integer.parseInt(prop.getProperty("uffaPrioritySundayNight"));
            uffaPrioritySaturdayMorning = Integer.parseInt(prop.getProperty("uffaPrioritySaturdayMorning"));
            uffaPrioritySaturdayAfternoon = Integer.parseInt(prop.getProperty("uffaPrioritySaturdayAfternoon"));
            uffaPrioritySaturdayNight = Integer.parseInt(prop.getProperty("uffaPrioritySaturdayNight"));
            uffaPriorityFridayAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityFridayAfternoon"));
            uffaPriorityFridayNight = Integer.parseInt(prop.getProperty("uffaPriorityFridayNight"));
            uffaPrioritySimple = Integer.parseInt(prop.getProperty("uffaPrioritySimple"));
            uffaPriorityNight = Integer.parseInt(prop.getProperty("uffaPriorityNight"));
            uffaPriorityDefaultHoliday = Integer.parseInt(prop.getProperty("uffaPriorityDefaultHoliday"));
            uffaPriorityDefaultHolidayNight = Integer.parseInt(prop.getProperty("uffaPriorityDefaultHolidayNight"));
            uffaPriorityVigiliaDiCapodannoMorning = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiCapodannoMorning"));
            uffaPriorityVigiliaDiCapodannoAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiCapodannoAfternoon"));
            uffaPriorityVigiliaDiCapodannoNight = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiCapodannoNight"));
            uffaPriorityCapodannoMorning = Integer.parseInt(prop.getProperty("uffaPriorityCapodannoMorning"));
            uffaPriorityCapodannoAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityCapodannoAfternoon"));
            uffaPriorityCapodannoNight = Integer.parseInt(prop.getProperty("uffaPriorityCapodannoNight"));
            uffaPriorityPasquaMorning = Integer.parseInt(prop.getProperty("uffaPriorityPasquaMorning"));
            uffaPriorityPasquaAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityPasquaAfternoon"));
            uffaPriorityPasquaNight = Integer.parseInt(prop.getProperty("uffaPriorityPasquaNight"));
            uffaPriorityLunediDellAngeloMorning = Integer.parseInt(prop.getProperty("uffaPriorityLunediDellAngeloMorning"));
            uffaPriorityLunediDellAngeloAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityLunediDellAngeloAfternoon"));
            uffaPriorityLunediDellAngeloNight = Integer.parseInt(prop.getProperty("uffaPriorityLunediDellAngeloNight"));
            uffaPriorityFerragostoOAssunzioneMorning = Integer.parseInt(prop.getProperty("uffaPriorityFerragostoOAssunzioneMorning"));
            uffaPriorityFerragostoOAssunzioneAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityFerragostoOAssunzioneAfternoon"));
            uffaPriorityFerragostoOAssunzioneNight = Integer.parseInt(prop.getProperty("uffaPriorityFerragostoOAssunzioneNight"));
            uffaPriorityVigiliaDiNataleMorning = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiNataleMorning"));
            uffaPriorityVigiliaDiNataleAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiNataleAfternoon"));
            uffaPriorityVigiliaDiNataleNight = Integer.parseInt(prop.getProperty("uffaPriorityVigiliaDiNataleNight"));
            uffaPriorityNataleMorning = Integer.parseInt(prop.getProperty("uffaPriorityNataleMorning"));
            uffaPriorityNataleAfternoon = Integer.parseInt(prop.getProperty("uffaPriorityNataleAfternoon"));
            uffaPriorityNataleNight = Integer.parseInt(prop.getProperty("uffaPriorityNataleNight"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
            switch (holiday.getName()) {
                //case of "standard holidays"
                case "Epifania":
                case "Festa della Liberazione":
                case "Festa del Lavoro":
                case "Festa della Repubblica":
                case "Tutti i santi":
                case "Immacolata Concezione":
                case "Santo Stefano":

                    Scocciatura scocciaturaHolidayMorning = new ScocciaturaVacanza(uffaPriorityDefaultHoliday, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaHolidayAfternoon = new ScocciaturaVacanza(uffaPriorityDefaultHoliday, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaHolidayNight = new ScocciaturaVacanza(uffaPriorityDefaultHolidayNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaHolidayMorning);
                    scocciaturaDAO.save(scocciaturaHolidayAfternoon);
                    scocciaturaDAO.save(scocciaturaHolidayNight);
                    break;

                case "Vigilia di Capodanno":
                    Scocciatura scocciaturaVigiliaDiCapodannoMorning = new ScocciaturaVacanza(uffaPriorityVigiliaDiCapodannoMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaVigiliaDiCapodannoAfternoon = new ScocciaturaVacanza(uffaPriorityVigiliaDiCapodannoAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaVigiliaDiCapodannoNight = new ScocciaturaVacanza(uffaPriorityVigiliaDiCapodannoNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaVigiliaDiCapodannoMorning);
                    scocciaturaDAO.save(scocciaturaVigiliaDiCapodannoAfternoon);
                    scocciaturaDAO.save(scocciaturaVigiliaDiCapodannoNight);
                    break;

                case "Capodanno":
                    Scocciatura scocciaturaCapodannoMorning = new ScocciaturaVacanza(uffaPriorityCapodannoMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaCapodannoAfternoon = new ScocciaturaVacanza(uffaPriorityCapodannoAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaCapodannoNight = new ScocciaturaVacanza(uffaPriorityCapodannoNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaCapodannoMorning);
                    scocciaturaDAO.save(scocciaturaCapodannoAfternoon);
                    scocciaturaDAO.save(scocciaturaCapodannoNight);
                    break;

                case "Pasqua":
                    Scocciatura scocciaturaPasquaMorning = new ScocciaturaVacanza(uffaPriorityPasquaMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaPasquaAfternoon = new ScocciaturaVacanza(uffaPriorityPasquaAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaPasquaNight = new ScocciaturaVacanza(uffaPriorityPasquaNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaPasquaMorning);
                    scocciaturaDAO.save(scocciaturaPasquaAfternoon);
                    scocciaturaDAO.save(scocciaturaPasquaNight);
                    break;

                case "Lunedì dell'Angelo":
                    Scocciatura scocciaturaLunediDellAngeloMorning = new ScocciaturaVacanza(uffaPriorityLunediDellAngeloMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaLunediDellAngeloAfternoon = new ScocciaturaVacanza(uffaPriorityLunediDellAngeloAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaLunediDellAngeloNight = new ScocciaturaVacanza(uffaPriorityLunediDellAngeloNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaLunediDellAngeloMorning);
                    scocciaturaDAO.save(scocciaturaLunediDellAngeloAfternoon);
                    scocciaturaDAO.save(scocciaturaLunediDellAngeloNight);
                    break;

                case "Ferragosto o Assunzione":
                    Scocciatura scocciaturaFerragostoOAssunzioneMorning = new ScocciaturaVacanza(uffaPriorityFerragostoOAssunzioneMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaFerragostoOAssunzioneAfternoon = new ScocciaturaVacanza(uffaPriorityFerragostoOAssunzioneAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaFerragostoOAssunzioneNight = new ScocciaturaVacanza(uffaPriorityFerragostoOAssunzioneNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaFerragostoOAssunzioneMorning);
                    scocciaturaDAO.save(scocciaturaFerragostoOAssunzioneAfternoon);
                    scocciaturaDAO.save(scocciaturaFerragostoOAssunzioneNight);
                    break;

                case "Vigilia di Natale":
                    Scocciatura scocciaturaVigiliaDiNataleMorning = new ScocciaturaVacanza(uffaPriorityVigiliaDiNataleMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaVigiliaDiNataleAfternoon = new ScocciaturaVacanza(uffaPriorityVigiliaDiNataleAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaVigiliaDiNataleNight = new ScocciaturaVacanza(uffaPriorityVigiliaDiNataleNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaVigiliaDiNataleMorning);
                    scocciaturaDAO.save(scocciaturaVigiliaDiNataleAfternoon);
                    scocciaturaDAO.save(scocciaturaVigiliaDiNataleNight);
                    break;

                case "Natale":
                    Scocciatura scocciaturaNataleMorning = new ScocciaturaVacanza(uffaPriorityNataleMorning, holiday, TimeSlot.MORNING);
                    Scocciatura scocciaturaNataleAfternoon = new ScocciaturaVacanza(uffaPriorityNataleAfternoon, holiday, TimeSlot.AFTERNOON);
                    Scocciatura scocciaturaNataleNight = new ScocciaturaVacanza(uffaPriorityNataleNight, holiday, TimeSlot.NIGHT);

                    scocciaturaDAO.save(scocciaturaNataleMorning);
                    scocciaturaDAO.save(scocciaturaNataleAfternoon);
                    scocciaturaDAO.save(scocciaturaNataleNight);
                    break;

            }

        }

    }


    private void registerConstraints() {

        ConfigVincoli configVincoli;
        try {
            File file = new File("src/main/resources/configVincoliDefault.properties");
            FileInputStream propsInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(propsInput);

            ConfigVincMaxPerCons confOver62 = new ConfigVincMaxPerCons(permanentConditionDAO.findByType("OVER 62"), Integer.parseInt(prop.getProperty("numMaxOreConsecutiveOver62")) * 60);
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

        //nessun turno può essere allocato a questa persona durante il suo smonto notte
        ConstraintTurniContigui constraint1 = new ConstraintTurniContigui(
                configVincoli.getHorizonNightShift(),
                ChronoUnit.HOURS,
                TimeSlot.NIGHT,
                new HashSet<>(Arrays.asList(TimeSlot.values()))
        );
        Constraint constraint2 = new ConstraintMaxPeriodoConsecutivo(configVincoli.getMaxConsecutiveTimeForEveryone());
        Constraint constraint4 = new ConstraintMaxOrePeriodo(configVincoli.getPeriodDaysNo(), configVincoli.getPeriodMaxTime());
        Constraint constraint5 = new ConstraintUbiquita();
        //Constraint constraint6 = new ConstraintNumeroDiRuoloTurno();
        Constraint constraint7 = new ConstraintHoliday();

        constraint1.setViolable(true);
        constraint2.setViolable(false);
        constraint4.setViolable(false);
        constraint5.setViolable(false);
        //constraint6.setViolable(false);
        constraint7.setViolable(true);

        for (ConfigVincMaxPerCons config : configVincoli.getConfigVincMaxPerConsPerCategoria()) {
            Constraint vincolo = new ConstraintMaxPeriodoConsecutivo(config.getMaxConsecutiveMinutes(), config.getConstrainedCondition());
            vincolo.setDescription("Constraint massimo periodo consecutivo per categoria " + config.getConstrainedCondition().getType());
            constraintDAO.saveAndFlush(vincolo);
        }
        constraint1.setDescription("Vincolo turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");
        constraint2.setDescription("Vincolo massimo periodo consecutivo. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        constraint4.setDescription("Vincolo massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        constraint5.setDescription("Vincolo ubiquità. Verifica che lo stesso medico non venga assegnato contemporaneamente a due turni diversi nello stesso giorno");
        //constraint6.setDescription("Vincolo numero utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno");
        constraint7.setDescription("Vincolo festività. Verifica che un medico che l'anno precedente ha lavorato durante una certa festività non venga assegnato a un turno corrispondente alla medesima festività.");

        constraintDAO.saveAndFlush(constraint1);
        constraintDAO.saveAndFlush(constraint2);
        constraintDAO.saveAndFlush(constraint4);
        constraintDAO.saveAndFlush(constraint5);
        //constraintDAO.saveAndFlush(constraint6);
        constraintDAO.saveAndFlush(constraint7);

    }

}
