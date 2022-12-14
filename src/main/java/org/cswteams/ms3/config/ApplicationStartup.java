package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component()
@Profile("!test")
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
    private CategoriaUtenteDao categoriaUtenteDao;

    @Autowired
    private IHolidayController holidayController;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        populateDB();
        //populateDBTestSchedule();
    }

    private void populateDBTestSchedule() {
        //Creo categorie
        CategoriaUtente categoriaOver62 = new CategoriaUtente(CategoriaUtentiEnum.OVER_62,LocalDate.now(), LocalDate.now().plusDays(1000));
        categoriaUtenteDao.save(categoriaOver62);

        //Creo utenti
        Utente u1 = new Utente("Manuel","Mastrofini", "MNLMSTR******", LocalDate.of(1987, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u7 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1950, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );

        u7.getCategorie().add(categoriaOver62);
        utenteDao.save(u7);
        utenteDao.save(u1);
        //creo servizi
        Servizio servizio1 = new Servizio("reparto");
        Servizio servizio2 = new Servizio("ambulatorio");
        servizioDao.save(servizio2);
        servizioDao.save(servizio1);
        //Creo turni
        HashSet<CategoriaUtentiEnum> categorieVietate= new HashSet<>(Arrays.asList(
                CategoriaUtentiEnum.DONNA_INCINTA,
                CategoriaUtentiEnum.OVER_62,
                CategoriaUtentiEnum.IN_MALATTIA,
                CategoriaUtentiEnum.IN_FERIE)
        );


        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>());
        t2.setNumUtentiGuardia(1);
        t2.setNumUtentiReperibilita(1);

        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate);
        t3.setNumUtentiGuardia(1);
        t3.setNumUtentiReperibilita(1);

        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate);
        t4.setNumUtentiGuardia(1);
        t4.setNumUtentiReperibilita(1);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, new HashSet<>());
        t5.setNumUtentiGuardia(1);
        t5.setNumUtentiReperibilita(1);

        turnoDao.save(t2);
        turnoDao.save(t3);
        turnoDao.save(t4);
        turnoDao.save(t5);

        try {
            LoadHoliday();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
        }

    }

    private void populateDB(){

        //Creo categorie
        CategoriaUtente categoriaOver62 = new CategoriaUtente(CategoriaUtentiEnum.OVER_62,LocalDate.now(), LocalDate.now().plusDays(1000));
        categoriaUtenteDao.save(categoriaOver62);

        //Creo utenti
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u6 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);

        Utente u7 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1950, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );
        u7.getCategorie().add(categoriaOver62);

        Utente u8 = new Utente("Manuel","Mastrofini", "MNLMASTR******", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u9 = new Utente("Giulia","Cantone", "GLCCTN******", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u10 = new Utente("Fabio","Valenzi", "FBVVLZ******", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u11 = new Utente("Giada","Rossi", "******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u12 = new Utente("Camilla","Verdi", "******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u13 = new Utente("Federica","Pollini", "******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u14 = new Utente("Claudia","Rossi", "******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u15 = new Utente("Giorgio","Bianchi", "******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u16 = new Utente("Claudio","Gialli", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);

        u1 = utenteDao.saveAndFlush(u1);
        u2 = utenteDao.saveAndFlush(u2);
        u3 = utenteDao.saveAndFlush(u3);
        u4 = utenteDao.saveAndFlush(u4);
        u5 = utenteDao.saveAndFlush(u5);
        u6 = utenteDao.saveAndFlush(u6);
        u7 = utenteDao.saveAndFlush(u7);
        u8 = utenteDao.saveAndFlush(u8);
        u9 = utenteDao.saveAndFlush(u9);
        u10 = utenteDao.saveAndFlush(u10);
        u11 = utenteDao.saveAndFlush(u11);
        u12 = utenteDao.saveAndFlush(u12);
        u13 = utenteDao.saveAndFlush(u13);
        u14 = utenteDao.saveAndFlush(u14);
        u15 = utenteDao.saveAndFlush(u15);
        u16 = utenteDao.saveAndFlush(u16);

        //creo servizi
        Servizio servizio1 = new Servizio("reparto");
        Servizio servizio2 = new Servizio("ambulatorio");

        servizioDao.save(servizio2);
        servizioDao.save(servizio1);


        //Creo turni
        HashSet<CategoriaUtentiEnum> categorieVietate= new HashSet<>(Arrays.asList(
                CategoriaUtentiEnum.DONNA_INCINTA,
                CategoriaUtentiEnum.OVER_62,
                CategoriaUtentiEnum.IN_MALATTIA,
                CategoriaUtentiEnum.IN_FERIE)
        );


        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>());
        t2.setNumUtentiGuardia(2);
        t2.setNumUtentiReperibilita(2);

        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate);
        t3.setNumUtentiGuardia(2);
        t3.setNumUtentiReperibilita(2);

        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate);
        t4.setNumUtentiGuardia(2);
        t4.setNumUtentiReperibilita(2);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, new HashSet<>());
        t5.setNumUtentiGuardia(2);
        t5.setNumUtentiReperibilita(2);

        turnoDao.saveAndFlush(t2);
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t4);
        turnoDao.saveAndFlush(t5);

        //creo associazioni
        Set<Utente> setUtenti1 = new HashSet<>();
        setUtenti1.add(u1);
        setUtenti1.add(u4);

        Set<Utente> setUtenti2 = new HashSet<>();
        setUtenti2.add(u2);
        setUtenti2.add(u5);

        Set<Utente> setUtenti3 = new HashSet<>();
        setUtenti3.add(u11);
        setUtenti3.add(u13);

        Set<Utente> setUtenti4 = new HashSet<>();
        setUtenti4.add(u8);
        setUtenti4.add(u9);

        Set<Utente> setUtenti5 = new HashSet<>();
        setUtenti5.add(u10);
        setUtenti5.add(u16);

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

        try {
            LoadHoliday();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
        }

    }


    /** Metodo che server per caricare le festivit?? dell'anno 2023/2024*/
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