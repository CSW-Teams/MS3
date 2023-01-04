package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ServizioDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Component()
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Autowired
    private IHolidayController holidayController;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u6 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u7 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1950, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );
        Utente u8 = new Utente("Manuel","Mastrofini", "MNLMASTR******", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u9 = new Utente("Giulia","Cantone", "GLCCTN******", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u10 = new Utente("Fabio","Valenzi", "FBVVLZ******", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", RuoloEnum.SPECIALIZZANDO);

        u1 = utenteDao.save(u1);
        u2 = utenteDao.save(u2);
        u3 = utenteDao.save(u3);
        u4 = utenteDao.save(u4);
        u5 = utenteDao.save(u5);
        u6 = utenteDao.save(u6);
        u7 = utenteDao.save(u7);
        u8 = utenteDao.save(u8);
        u9 = utenteDao.save(u9);
        u10 = utenteDao.save(u10);

        Servizio servizio1 = new Servizio("reparto");
        servizioDao.save(servizio1);

        Servizio servizio2 = new Servizio("ambulatorio");
        servizioDao.save(servizio2);

        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO);
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO);
        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO);
        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO);
      

        turnoDao.save(t2);
        turnoDao.save(t3);
        turnoDao.save(t4);
        turnoDao.save(t5);

        Set<Utente> setUtenti1 = new HashSet<>();
        setUtenti1.add(u1);
        setUtenti1.add(u4);

        Set<Utente> setUtenti2 = new HashSet<>();
        setUtenti2.add(u2);
        setUtenti2.add(u5);

        Set<Utente> setUtenti3 = new HashSet<>();
        setUtenti3.add(u7);
        setUtenti3.add(u3);

        Set<Utente> setUtenti4 = new HashSet<>();
        setUtenti4.add(u8);
        setUtenti4.add(u9);

        Set<Utente> setUtenti5 = new HashSet<>();
        setUtenti5.add(u10);
        setUtenti5.add(u6);

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,20),t5,setUtenti3,setUtenti5));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,19),t2,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,19),t3,setUtenti2,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,19),t4,setUtenti2,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,20),t2,setUtenti1,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,20),t3,setUtenti3,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,20),t4,setUtenti3,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,21),t2,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,21),t3,setUtenti4,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,21),t4,setUtenti4,setUtenti2));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,22),t2,setUtenti3,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,22),t3,setUtenti2,setUtenti1));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,22),t4,setUtenti2,setUtenti1));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,22),t5,setUtenti3,setUtenti2));


        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,23),t2,setUtenti4,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,23),t3,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,23),t4,setUtenti1,setUtenti3));


        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,24),t2,setUtenti1,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,24),t3,setUtenti2,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,24),t4,setUtenti2,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,24),t5,setUtenti1,setUtenti3));


        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,25),t2,setUtenti3,setUtenti1));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,25),t3,setUtenti4,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,25),t4,setUtenti4,setUtenti2));



        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,26),t5,setUtenti3,setUtenti5));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,26),t2,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,26),t3,setUtenti2,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,26),t4,setUtenti2,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,27),t2,setUtenti1,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,27),t3,setUtenti3,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,27),t4,setUtenti3,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,28),t2,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,28),t3,setUtenti4,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,28),t4,setUtenti4,setUtenti2));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,29),t2,setUtenti3,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,29),t3,setUtenti2,setUtenti1));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,29),t4,setUtenti2,setUtenti1));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,29),t5,setUtenti3,setUtenti2));


        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,30),t2,setUtenti4,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,30),t3,setUtenti1,setUtenti3));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,30),t4,setUtenti1,setUtenti3));


        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,31),t2,setUtenti1,setUtenti2));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,31),t3,setUtenti2,setUtenti4));
        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,31),t4,setUtenti2,setUtenti4));

        assegnazioneTurnoDao.save(new AssegnazioneTurno(LocalDate.of(2022,12,31),t5,setUtenti1,setUtenti3));

        LoadHoliday();
    }


    /** Metodo che server per caricare le festività dell'anno 2023/2024*/
    public void LoadHoliday() throws IOException {
        List<List<String>> data = new ArrayList<>();
        String currPath = System.getProperty("user.dir");
        String filePath = currPath+"\\src\\main\\resources\\holiday.csv";
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        while(line != null)
        {
            List<String> lineData = Arrays.asList(line.split(";"));//splitting lines
            data.add(lineData);
            line = br.readLine();
        }
        for(List<String> list : data) {
            String HolidayData = Arrays.asList(list.get(0).split(";")).get(0).toString();
            final String[] HolidayDataS = HolidayData.split("/");
            int year = Integer.parseInt(HolidayDataS[2].replaceAll("[^0-9]", ""));
            int month = Integer.parseInt(HolidayDataS[1].replaceAll("[^0-9]", ""));
            int day = Integer.parseInt(HolidayDataS[0].replaceAll("[^0-9]", ""));
            String HolidayName= Arrays.asList(list.get(1).split(";")).get(0);
            String HolidayLocation= Arrays.asList(list.get(2).split(";")).get(0);
            String Holiday_Category= Arrays.asList(list.get(3).split(";")).get(0);
            LocalDate Date = LocalDate.of(year, month, day);
            holidayController.registerHolidayPeriod(new HolidayDTO(
                    HolidayName,
                    HolidayCategory.valueOf(Holiday_Category),
                    Date.toEpochDay(),
                    Date.toEpochDay(),
                    HolidayLocation
            ));
        }
        br.close();
    }
}