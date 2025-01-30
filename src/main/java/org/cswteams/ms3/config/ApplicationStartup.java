package org.cswteams.ms3.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.cswteams.ms3.config.multitenancy.TenantConfig;
import org.cswteams.ms3.control.medicalService.IMedicalServiceController;
import org.cswteams.ms3.control.preferenze.CalendarSetting;
import org.cswteams.ms3.control.preferenze.CalendarSettingBuilder;
import org.cswteams.ms3.control.preferenze.ICalendarServiceManager;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.dto.holidays.RetrieveHolidaysDTOIn;
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
import org.cswteams.ms3.exception.ShiftException;
import org.cswteams.ms3.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    private static final String DEFAULT_SCHEMA = "public";

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
    private DoctorUffaPrioritySnapshotDAO doctorUffaPrioritySnapshotDAO;

    @Autowired
    private DoctorHolidaysDAO doctorHolidaysDAO;
    @Autowired
    private ScheduleDAO scheduleDAO;

    @Autowired
    private SystemUserDAO systemUserDAO;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
      //  if (doctorDAO.count() == 0) {

            ObjectMapper objectMapper = new ObjectMapper();
            TenantConfig tenantConfig = objectMapper.readValue(new File("src/main/resources/tenants_config.json"), TenantConfig.class);
            List<String> tenantSchemas = tenantConfig.getTenants();

            for (String tenant : tenantSchemas) {
                changeSchema(tenant.toLowerCase());
                registerHolidays();
            }

            changeSchema(DEFAULT_SCHEMA);
            populatePublicDB();

            try {
                for (String tenant : tenantSchemas) {
                    changeSchema(tenant.toLowerCase());
                    populateTenantDB(tenant);
                    registerConstraints();
                    registerScocciature();
                }
            } catch (ShiftException e) {
                e.printStackTrace();
            }

          // Ripristina lo schema di default ("public" per PostgreSQL)
            changeSchema(DEFAULT_SCHEMA);

     //   }

    }

    private void changeSchema(String tenant) {
        TenantContext.setCurrentTenant(tenant);
    }


    /**
     * Function which has the responsibility to register the holidays into the system, if it has not been registered yet.
     */
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



    /**
     * Function which has the responsibility to define the holidays Vigilia di Natale and Vigilia di Capodanno, if necessary.
     * @param holidayDTO Already defined holiday (normally Natale) from which the new holidays will be defined and created
     * @return List of new holiday DTOs
     */
    private static List<HolidayDTO> getNewHolidayDTOs(HolidayDTO holidayDTO) {
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


    private void populatePublicDB() {

        //Creo utenti
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        SystemUser u3 = new SystemUser("Federica", "Villani", "VLLFRC98P43H926Y", LocalDate.of(1998, 9, 3), "federicavillani.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "A");
        SystemUser u4 = new SystemUser("Daniele", "Colavecchi", "CLVDNL82C21H501E", LocalDate.of(1982, 7, 6), "danielecolavecchi.tenantb@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "B");
        SystemUser u5 = new SystemUser("Daniele", "La Prova", "LPRDNL98H13H501F", LocalDate.of(1998, 2, 12), "danielelaprova.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "A");
        SystemUser u7 = new SystemUser("Luca", "Fiscariello", "FSCLCU99D15A783Z", LocalDate.of(1998, 8, 12), "lucafiscariello.tenantb@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "B");
        SystemUser u8_1 = new SystemUser("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "A");
        SystemUser u8_2 = new SystemUser("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.PLANNER), "B");
        SystemUser u10_1 = new SystemUser("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "A");
        SystemUser u10_2 = new SystemUser("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.DOCTOR), "B");
        SystemUser u9 = new SystemUser("Giulia", "Cantone II", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "A");
        SystemUser u1_1 = new SystemUser("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.CONFIGURATOR), "A");
        SystemUser u1_2 = new SystemUser("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.CONFIGURATOR), "B");
        SystemUser u2 = new SystemUser("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde.tenantb@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR), "B");
        SystemUser u6_1 = new SystemUser("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.PLANNER, SystemActor.DOCTOR), "A");
        SystemUser u6_2 = new SystemUser("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.CONFIGURATOR), "B");
        SystemUser u44_1 = new SystemUser("Giulio", "Farnasini", "GLIFNS94M07G224O", LocalDate.of(1994, 8, 7), "giuliofarnasini.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.PLANNER), "A");
        SystemUser u44_2 = new SystemUser("Giulio", "Farnasini", "GLIFNS94M07G224O", LocalDate.of(1994, 8, 7), "giuliofarnasini.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.DOCTOR), "B");
        SystemUser u45_1 = new SystemUser("Full", "Permessi", "FLLPRM98M24G224O", LocalDate.of(1998, 8, 24), "fullpermessi.tenanta@gmail.com", encoder.encode("passw"), Set.of(SystemActor.DOCTOR, SystemActor.PLANNER, SystemActor.CONFIGURATOR), "A");
        SystemUser u45_2 = new SystemUser("Full", "Permessi", "FLLPRM98M24G224O", LocalDate.of(1998, 8, 24), "fullpermessi.tenantb@gmail.com", encoder.encode("passw2"), Set.of(SystemActor.DOCTOR, SystemActor.PLANNER, SystemActor.CONFIGURATOR), "B");

        u1_1 = systemUserDAO.saveAndFlush(u1_1);
        u1_2 = systemUserDAO.saveAndFlush(u1_2);
        u2 = systemUserDAO.saveAndFlush(u2);
        u3 = systemUserDAO.saveAndFlush(u3);
        u4 = systemUserDAO.saveAndFlush(u4);
        u5 = systemUserDAO.saveAndFlush(u5);
        u6_1 = systemUserDAO.saveAndFlush(u6_1);
        u6_2 = systemUserDAO.saveAndFlush(u6_2);
        u7 = systemUserDAO.saveAndFlush(u7);
        u8_1 = systemUserDAO.saveAndFlush(u8_1);
        u8_2 = systemUserDAO.saveAndFlush(u8_2);
        u9 = systemUserDAO.saveAndFlush(u9);
        u10_1 = systemUserDAO.saveAndFlush(u10_1);
        u10_2 = systemUserDAO.saveAndFlush(u10_2);
        u44_1 = systemUserDAO.saveAndFlush(u44_1);
        u45_1 = systemUserDAO.saveAndFlush(u45_1);
        u44_2 = systemUserDAO.saveAndFlush(u44_2);
        u45_2 = systemUserDAO.saveAndFlush(u45_2);
    }


    private void populateTenantDB(String tenant) throws ShiftException {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        // Condition may be structure specific TODO: Ask if it is needed a configuration file for that
        PermanentCondition over62 = new PermanentCondition("OVER 62");
        TemporaryCondition pregnant = new TemporaryCondition("INCINTA", LocalDate.now().toEpochDay(), LocalDate.now().plusMonths(9).toEpochDay());
        TemporaryCondition maternity = new TemporaryCondition("IN MATERNITA'", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(60).toEpochDay());
        TemporaryCondition vacation = new TemporaryCondition("IN FERIE", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());
        TemporaryCondition sick = new TemporaryCondition("IN MALATTIA", LocalDate.now().toEpochDay(), LocalDate.now().plusDays(7).toEpochDay());
        // Save in persistence all possible conditions
        permanentConditionDAO.save(over62);
        temporaryConditionDAO.save(vacation);
        temporaryConditionDAO.save(pregnant);
        temporaryConditionDAO.save(sick);
        temporaryConditionDAO.save(maternity);

        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE (INCLUSIVE)
        Specialization cardiologia = new Specialization("CARDIOLOGIA");
        Specialization oncologia = new Specialization("ONCOLOGIA");
        // Save in persistence all possible specialization
        specializationDAO.save(cardiologia);
        specializationDAO.save(oncologia);

        Task ward = new Task(TaskEnum.WARD);
        Task clinic = new Task(TaskEnum.CLINIC);
        Task emergency = new Task(TaskEnum.EMERGENCY);
        Task operatingRoom = new Task(TaskEnum.OPERATING_ROOM);
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

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


        if (tenant.equals("A")) {
            Doctor u3 = new Doctor("Federica", "Villani", "VLLFRC98P43H926Y", LocalDate.of(1998, 9, 3), "federicavillani.tenanta@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
            Doctor u5 = new Doctor("Daniele", "La Prova", "LPRDNL98H13H501F", LocalDate.of(1998, 2, 12), "danielelaprova.tenanta@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
            Doctor u8_1 = new Doctor("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini.tenanta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
            Doctor u10_1 = new Doctor("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi.tenanta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
            u3 = doctorDAO.saveAndFlush(u3);
            u5 = doctorDAO.saveAndFlush(u5);
            u8_1 = doctorDAO.saveAndFlush(u8_1);
            u10_1 = doctorDAO.saveAndFlush(u10_1);

        } else {
            Doctor u4 = new Doctor("Daniele", "Colavecchi", "CLVDNL82C21H501E", LocalDate.of(1982, 7, 6), "danielecolavecchi.tenantb@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
            Doctor u7 = new Doctor("Luca", "Fiscariello", "FSCLCU99D15A783Z", LocalDate.of(1998, 8, 12), "lucafiscariello.tenantb@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
            Doctor u8_2 = new Doctor("Manuel", "Mastrofini", "MSTMNL80M20H501X", LocalDate.of(1988, 5, 4), "manuelmastrofini.tenantb@gmail.com", encoder.encode("passw2"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.PLANNER));
            Doctor u10_2 = new Doctor("Fabio", "Valenzi", "VLZFBA90A03H501U", LocalDate.of(1989, 12, 6), "fabiovalenzi.tenantb@gmail.com", encoder.encode("passw2"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));

            u4 = doctorDAO.saveAndFlush(u4);
            u7 = doctorDAO.saveAndFlush(u7);
            u8_2 = doctorDAO.saveAndFlush(u8_2);
            u10_2 = doctorDAO.saveAndFlush(u10_2);
        }

//        Doctor u11 = new Doctor("Giada", "Rossi", "RSSGDI92H68H501O", LocalDate.of(1997, 3, 14), "giada.rossi@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u12 = new Doctor("Camilla", "Verdi", "VRDCML95B41H501L", LocalDate.of(1997, 5, 23), "camilla.verdi@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u13 = new Doctor("Federica", "Pollini", "PLLFDR94S70H501I", LocalDate.of(1998, 2, 12), "federica.pollini@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u14 = new Doctor("Claudia", "Rossi II", "RSSCLD91C52H501A", LocalDate.of(1982, 7, 6), "claudia.rossi@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u15 = new Doctor("Giorgio", "Bianchi", "BNCGRG88E21H501S", LocalDate.of(1993, 2, 12), "giorgio.bianchi@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u16 = new Doctor("Claudio", "Gialli", "GLLCLD89B14H501T", LocalDate.of(1998, 8, 12), "claudia.gialli@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u17 = new Doctor("Filippo", "Neri", "NREFLP92R24H501C", LocalDate.of(1998, 2, 12), "filippo.neru@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u18 = new Doctor("Vincenzo", "Grassi", "GRSVNC60A19H501P", LocalDate.of(1998, 8, 12), "vincenzo.grassi@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u19 = new Doctor("Diana", "Pasquali", "PSQDNI97D62H501U", LocalDate.of(1997, 4, 22), "diana.pasquali@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u20 = new Doctor("Francesco", "Lo Presti", "LPSFRC66T05G071E", LocalDate.of(1998, 8, 12), "francesco.lopresti@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u21 = new Doctor("Andrea", "Pepe", "PPENDR99M05I150J", LocalDate.of(1999, 8, 5), "andrea.pepe@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u22 = new Doctor("Matteo", "Fanfarillo", "FNFMTT99E10A123E", LocalDate.of(1999, 5, 10), "matteo.fanfarillo99@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR, SystemActor.PLANNER));
//        Doctor u23 = new Doctor("Matteo", "Ciccaglione", "CCCMTT99H15C439X", LocalDate.of(1998, 6, 15), "matteo.ciccaglione@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u24 = new Doctor("Vittoria", "De Nitto", "DNTVTT60C59E612D", LocalDate.of(1998, 8, 12), "vittoria.denitto@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u25 = new Doctor("Valeria", "Cardellini", "CRDVLR68L44H501B", LocalDate.of(1998, 8, 12), "valeria.cardellini@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u26 = new Doctor("Roberto", "Monte", "MNTRBT69R01D666W", LocalDate.of(1998, 8, 12), "roberto.monte@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u27 = new Doctor("Giovanni", "Saggio", "SGGGVN65D30H501J", LocalDate.of(1998, 8, 12), "giovanni.saggio@gmail.com", encoder.encode("passw"), Seniority.STRUCTURED, Set.of(SystemActor.DOCTOR));
//        Doctor u28 = new Doctor("Livia", "Simoncini", "SMNLVI98L57H501S", LocalDate.of(1998, 7, 19), "livia.simoncini@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u29 = new Doctor("Ludovico", "Zarrelli", "ZRRLVC99E03G482P", LocalDate.of(1998, 5, 3), "ludovico.zerrelli@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u30 = new Doctor("Alessandro", "Montenegro", "MNTLSS96P20H501J", LocalDate.of(1998, 8, 12), "alessandro.montenegro@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u31 = new Doctor("Daniel", "Lungu", "LNGDNL98T04H501I", LocalDate.of(1998, 12, 4), "daniel.lungu@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u32 = new Doctor("Andrea", "Tosti", "TSTNDR97A10H501E", LocalDate.of(1998, 8, 12), "andrea.tosti@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u33 = new Doctor("Giorgio", "Pesce", "PSCGRG98E08H501T", LocalDate.of(1998, 8, 12), "giorgia.pesce@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u34 = new Doctor("Valerio", "Palmerini", "PLMVLR93B12H501U", LocalDate.of(1998, 8, 12), "valerio.palmerini@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
//        try {
//            userController.addSpecialization(u4, cardiologia);
//            userController.addSpecialization(u5, cardiologia);
//            userController.addSpecialization(u7, cardiologia);
//            userController.addSpecialization(u8, cardiologia);
//            userController.addSpecialization(u14, cardiologia);
//            userController.addSpecialization(u15, cardiologia);
//            userController.addSpecialization(u16, cardiologia);
//            userController.addSpecialization(u18, cardiologia);
//            userController.addSpecialization(u24, cardiologia);
//            userController.addSpecialization(u25, cardiologia);
//            userController.addSpecialization(u26, cardiologia);
//            userController.addSpecialization(u27, cardiologia);
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        Doctor u35 = new Doctor("Simone", "Bauco", "BCASMN00T01A123Y", LocalDate.of(2000, 12, 1), "simonebauco@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u36 = new Doctor("Simone", "Staccone", "STCSMN00M16D810O", LocalDate.of(2000, 8, 16), "simonestaccone@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u37 = new Doctor("Massimo", "Stanzione", "STNMSM96L12F205R", LocalDate.of(1996, 7, 12), "massimostanzione@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u38 = new Doctor("Danilo", "D'Amico", "DMCDNL99A08A345C", LocalDate.of(2000, 1, 8), "danilodamico@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u39 = new Doctor("Matteo", "Kobero", "FDRMTT98S20H501D", LocalDate.of(1998, 11, 20), "matteokobero@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u40 = new Doctor("Sebastian", "Opriscan", "PRSSST00D12H501L", LocalDate.of(2000, 4, 12), "sebastianopriscan@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        Doctor u41 = new Doctor("Simone", "Festa", "FSTSMN98E26H501N", LocalDate.of(1998, 5, 26), "simonefesta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
//        TenantUser u42 = new TenantUser("Fabio", "Armani", "RMNFBA50M12G156E", LocalDate.of(1950, 8, 12), "fabioarmani@gmail.com", encoder.encode("passw"), Set.of(SystemActor.CONFIGURATOR));
//        Doctor u43 = new Doctor("Sara","Da Canal","PLMVLR93B12H501U",LocalDate.of(1999,6,19),"saradacanal@gmail.com",encoder.encode("passw"),Seniority.SPECIALIST_SENIOR,Set.of(SystemActor.DOCTOR));

//        u11 = doctorDAO.saveAndFlush(u11);
//        u12 = doctorDAO.saveAndFlush(u12);
//        u13 = doctorDAO.saveAndFlush(u13);
//        u14 = doctorDAO.saveAndFlush(u14);
//        u15 = doctorDAO.saveAndFlush(u15);
//        u16 = doctorDAO.saveAndFlush(u16);
//        u17 = doctorDAO.saveAndFlush(u17);
//        u18 = doctorDAO.saveAndFlush(u18);
//        u19 = doctorDAO.saveAndFlush(u19);
//        u20 = doctorDAO.saveAndFlush(u20);
//        u21 = doctorDAO.saveAndFlush(u21);
//        u22 = doctorDAO.saveAndFlush(u22);
//        u23 = doctorDAO.saveAndFlush(u23);
//        u24 = doctorDAO.saveAndFlush(u24);
//        u25 = doctorDAO.saveAndFlush(u25);
//        u26 = doctorDAO.saveAndFlush(u26);
//        u27 = doctorDAO.saveAndFlush(u27);
//        u28 = doctorDAO.saveAndFlush(u28);
//        u29 = doctorDAO.saveAndFlush(u29);
//        u30 = doctorDAO.saveAndFlush(u30);
//        u31 = doctorDAO.saveAndFlush(u31);
//        u32 = doctorDAO.saveAndFlush(u32);
//        u33 = doctorDAO.saveAndFlush(u33);
//        u34 = doctorDAO.saveAndFlush(u34);
//        u35 = doctorDAO.saveAndFlush(u35);
//        u36 = doctorDAO.saveAndFlush(u36);
//        u37 = doctorDAO.saveAndFlush(u37);
//        u38 = doctorDAO.saveAndFlush(u38);
//        u39 = doctorDAO.saveAndFlush(u39);
//        u40 = doctorDAO.saveAndFlush(u40);
//        u41 = doctorDAO.saveAndFlush(u41);
//        u42 = userDAO.saveAndFlush(u42);
//        u43 = doctorDAO.saveAndFlush(u43);

        if (tenant.equals("A")) {
            Doctor u9 = new Doctor("Giulia", "Cantone II", "CTNGLI78E44H501Z", LocalDate.of(1991, 2, 12), "giuliacantone.tenanta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.DOCTOR));
            Doctor u1_1 = new Doctor("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97.tenanta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.CONFIGURATOR));
            Doctor u6_1 = new Doctor("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone.tenanta@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.PLANNER, SystemActor.DOCTOR));
            Doctor u44_1 = new Doctor("Giulio","Farnasini","GLIFNS94M07G224O",LocalDate.of(1994,8,7),"giuliofarnasini.tenanta@gmail.com",encoder.encode("passw"),Seniority.STRUCTURED,Set.of(SystemActor.PLANNER));
            Doctor u45_1 = new Doctor("Full","Permessi","FLLPRM98M24G224O",LocalDate.of(1998,8,24),"fullpermessi.tenanta@gmail.com",encoder.encode("passw"),Seniority.STRUCTURED,Set.of(SystemActor.DOCTOR, SystemActor.PLANNER, SystemActor.CONFIGURATOR));

            u1_1 = doctorDAO.saveAndFlush(u1_1);
            u6_1 = doctorDAO.saveAndFlush(u6_1);
            u9 = doctorDAO.saveAndFlush(u9);
            u44_1 = doctorDAO.saveAndFlush(u44_1);
            u45_1 = doctorDAO.saveAndFlush(u45_1);
        } else {
            Doctor u1_2 = new Doctor("Martina", "Salvati", "SLVMTN97T56H501Y", LocalDate.of(1997, 3, 14), "salvatimartina97.tenantb@gmail.com", encoder.encode("passw2"), Seniority.SPECIALIST_JUNIOR, Set.of(SystemActor.CONFIGURATOR));
            Doctor u2 = new Doctor("Domenico", "Verde", "VRDDMC96H16H501H", LocalDate.of(1997, 5, 23), "domenicoverde.tenantb@gmail.com", encoder.encode("passw"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.DOCTOR));
            Doctor u6_2 = new Doctor("Giovanni", "Cantone", "GVNCTN48M22D429G", LocalDate.of(1960, 3, 7), "giovannicantone.tenantb@gmail.com", encoder.encode("passw2"), Seniority.SPECIALIST_SENIOR, Set.of(SystemActor.CONFIGURATOR));
            Doctor u44_2 = new Doctor("Giulio","Farnasini","GLIFNS94M07G224O",LocalDate.of(1994,8,7),"giuliofarnasini.tenantb@gmail.com",encoder.encode("passw2"),Seniority.STRUCTURED,Set.of(SystemActor.DOCTOR));
            Doctor u45_2 = new Doctor("Full","Permessi","FLLPRM98M24G224O",LocalDate.of(1998,8,24),"fullpermessi.tenantb@gmail.com",encoder.encode("passw2"),Seniority.STRUCTURED,Set.of(SystemActor.DOCTOR, SystemActor.PLANNER, SystemActor.CONFIGURATOR));

            u1_2 = doctorDAO.saveAndFlush(u1_2);
            u2 = doctorDAO.saveAndFlush(u2);
            u6_2 = doctorDAO.saveAndFlush(u6_2);
            u44_2 = doctorDAO.saveAndFlush(u44_2);
            u45_2 = doctorDAO.saveAndFlush(u45_2);
        }


        /* HashMap<Seniority, Integer> doctorsNumberBySeniority = new HashMap<>();
        doctorsNumberBySeniority.put(Seniority.STRUCTURED, 1);
        doctorsNumberBySeniority.put(Seniority.SPECIALIST_SENIOR, 1);
        doctorsNumberBySeniority.put(Seniority.SPECIALIST_JUNIOR, 1); */

        List<QuantityShiftSeniority> quantityShiftSeniorityList1 = new ArrayList<>();
        for(Task t:ambulatorioCardiologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityList1.add(quantityShiftSeniority);
        }
        List<QuantityShiftSeniority> quantityShiftSeniorityList2 = new ArrayList<>();
        for(Task t:ambulatorioCardiologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityList2.add(quantityShiftSeniority);
        }
        List<QuantityShiftSeniority> quantityShiftSeniorityList3 = new ArrayList<>();
        for(Task t:ambulatorioCardiologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityList3.add(quantityShiftSeniority);
        }

        List<QuantityShiftSeniority> quantityShiftSeniorityListOncologia1 = new ArrayList<>();
        for(Task t:ambulatorioOncologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityListOncologia1.add(quantityShiftSeniority);
        }
        List<QuantityShiftSeniority> quantityShiftSeniorityListOncologia2 = new ArrayList<>();
        for(Task t:ambulatorioOncologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityListOncologia2.add(quantityShiftSeniority);
        }
        List<QuantityShiftSeniority> quantityShiftSeniorityListOncologia3 = new ArrayList<>();
        for(Task t:ambulatorioOncologia.getTasks()) {
            Map<Seniority,Integer> mapSeniorityQuantity=new HashMap<>();
            mapSeniorityQuantity.put(Seniority.STRUCTURED,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_SENIOR,1);
            mapSeniorityQuantity.put(Seniority.SPECIALIST_JUNIOR,1);
            QuantityShiftSeniority quantityShiftSeniority  = new QuantityShiftSeniority(mapSeniorityQuantity,t);
            quantityShiftSeniorityListOncologia3.add(quantityShiftSeniority);
        }

        Set<DayOfWeek> allDaysOfWeek = new HashSet<>();
        allDaysOfWeek.add(DayOfWeek.MONDAY);
        allDaysOfWeek.add(DayOfWeek.TUESDAY);
        allDaysOfWeek.add(DayOfWeek.WEDNESDAY);
        allDaysOfWeek.add(DayOfWeek.THURSDAY);
        allDaysOfWeek.add(DayOfWeek.FRIDAY);
        allDaysOfWeek.add(DayOfWeek.SATURDAY);
        allDaysOfWeek.add(DayOfWeek.SUNDAY);

        Set<DayOfWeek> mondayAndTuesday = new HashSet<>();
        mondayAndTuesday.add(DayOfWeek.MONDAY);
        mondayAndTuesday.add(DayOfWeek.TUESDAY);

        Shift shift1 = new Shift(LocalTime.of(1, 0),
                Duration.ofHours(6),
                ambulatorioCardiologia,
                TimeSlot.NIGHT,
                quantityShiftSeniorityList1,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift1);

        Shift shift2 = new Shift(LocalTime.of(8, 0),
                Duration.ofHours(6),
                ambulatorioCardiologia,
                TimeSlot.MORNING,
                quantityShiftSeniorityList2,
                mondayAndTuesday,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift2);

        Shift shift3 = new Shift(LocalTime.of(15, 0),
                Duration.ofHours(6),
                ambulatorioCardiologia,
                TimeSlot.AFTERNOON,
                quantityShiftSeniorityList3,
                allDaysOfWeek,
                Collections.emptyList());
        shiftDAO.saveAndFlush(shift3);

//        Shift shift4 = new Shift(LocalTime.of(1, 0),
//                Duration.ofHours(6),
//                ambulatorioOncologia,
//                TimeSlot.NIGHT,
//                quantityShiftSeniorityListOncologia1,
//                allDaysOfWeek,
//                Collections.emptyList());
//        shiftDAO.saveAndFlush(shift4);
//
//        Shift shift5 = new Shift(LocalTime.of(8, 0),
//                Duration.ofHours(6),
//                ambulatorioOncologia,
//                TimeSlot.MORNING,
//                quantityShiftSeniorityListOncologia2,
//                allDaysOfWeek,
//                Collections.emptyList());
//        shiftDAO.saveAndFlush(shift5);
//
//        Shift shift6 = new Shift(LocalTime.of(15, 0),
//                Duration.ofHours(6),
//                ambulatorioOncologia,
//                TimeSlot.AFTERNOON,
//                quantityShiftSeniorityListOncologia3,
//                allDaysOfWeek,
//                Collections.emptyList());
//        shiftDAO.saveAndFlush(shift6);

        //creation of the DoctorHolidays instances
        List<Doctor> allDoctors = doctorDAO.findAll();
        List<Holiday> holidays = holidayDAO.findAll();  //retrieve of holiday entities (and not DTOs)

        //we are assuming that, at the moment of instantiation of DoctorHolidays, the corresponding doctor has worked in no concrete shift in the past.
        HashMap<Holiday, Boolean> holidayMap = new HashMap<>();
        for(Holiday holiday: holidays) {
            if(!holiday.getName().equals("Domenica"))   //we do not care about Sundays as holidays
                holidayMap.put(holiday, false);

        }

        for(Doctor doctor: allDoctors) {
            DoctorUffaPriority dup = new DoctorUffaPriority(doctor);
            DoctorUffaPrioritySnapshot doctorUffaPrioritySnapshot = new DoctorUffaPrioritySnapshot(doctor);
            DoctorHolidays dh = new DoctorHolidays(doctor, holidayMap);

            doctorUffaPriorityDAO.save(dup);
            doctorHolidaysDAO.save(dh);
            doctorUffaPrioritySnapshotDAO.save(doctorUffaPrioritySnapshot);

        }

        //TODO: Eliminare in seguito
        /*List<ConcreteShift> lc= new ArrayList<>();
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
        }*/

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

    }

}
