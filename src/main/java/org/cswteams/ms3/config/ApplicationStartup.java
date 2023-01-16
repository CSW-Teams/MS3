package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.control.vincoli.VincoloTipologieTurniContigue;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;
import org.cswteams.ms3.enums.HolidayCategory;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.*;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    private ScheduleDao dao;

    @Autowired
    private VincoloTipologieTurniContigueDao vincoloTipologieTurniContigueDao;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        populateDB();
        registerHolidays();
        registerConstraints();
        //populateDBTestSchedule();
    }

    private void registerConstraints(){
        // nessun turno può essere allocato a questa persona durante il suo smonto notte
        VincoloTipologieTurniContigue vincoloTurniContigui = new VincoloTipologieTurniContigue(
            20,
            ChronoUnit.HOURS,
            TipologiaTurno.NOTTURNO,
            new HashSet<>(Arrays.asList(TipologiaTurno.values()))
            );
        vincoloTipologieTurniContigueDao.save(vincoloTurniContigui);
    }
    
    private void registerHolidays(){
        try {
            LoadHoliday();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
        }

        // registra le domeniche nel ventennio 2013-2033
        holidayController.registerSundays(LocalDate.of(2013, 1, 1), 20);

    }

    private void populateDBTestSchedule() throws  TurnoException {
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


        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>(),false);
        t2.setNumUtentiGuardia(1);
        t2.setNumUtentiReperibilita(1);

        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(23, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate,false);
        t3.setNumUtentiGuardia(1);
        t3.setNumUtentiReperibilita(1);

        Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate,false);
        t4.setNumUtentiGuardia(1);
        t4.setNumUtentiReperibilita(1);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, new HashSet<>(),false);
        t5.setNumUtentiGuardia(1);
        t5.setNumUtentiReperibilita(1);

        turnoDao.save(t2);
        turnoDao.save(t3);
        turnoDao.save(t4);
        turnoDao.save(t5);
    }

    private void populateDB() throws TurnoException {

        //Creo categorie
        CategoriaUtente categoriaOver62 = new CategoriaUtente(CategoriaUtentiEnum.OVER_62,LocalDate.of(2022,3,7), LocalDate.now().plusDays(1000));
        categoriaUtenteDao.save(categoriaOver62);

        CategoriaUtente ferie = new CategoriaUtente(CategoriaUtentiEnum.IN_FERIE,LocalDate.now(), LocalDate.now().plusDays(7));
        categoriaUtenteDao.save(ferie);

        //Creo utenti
        Utente u6 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1960, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );
        u6.getCategorie().add(categoriaOver62);
        u6.getCategorie().add(ferie);
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u7 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u8 = new Utente("Manuel","Mastrofini", "MNLMASTR******", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u9 = new Utente("Giulia","Cantone", "GLCCTN******", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u10 = new Utente("Fabio","Valenzi", "FBVVLZ******", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u11 = new Utente("Giada","Rossi", "******", LocalDate.of(1997, 3, 14),"**@gmail.com", RuoloEnum.SPECIALIZZANDO );
        Utente u12 = new Utente("Camilla","Verdi", "******", LocalDate.of(1997, 5, 23),"***@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u13 = new Utente("Federica","Pollini", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u14 = new Utente("Claudia","Rossi", "******", LocalDate.of(1982, 7, 6),"***@gmail.com@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u15 = new Utente("Giorgio","Bianchi", "******", LocalDate.of(1993, 2, 12),"***@gmail.com@gmail.com", RuoloEnum.STRUTTURATO);
        Utente u16 = new Utente("Claudio","Gialli", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u17 = new Utente("Filippo","Neri", "******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u18 = new Utente("Vincenzo","Grassi", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u19 = new Utente("Diana","Pasquali", "******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u20 = new Utente("Francesco","Lo Presti", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u21 = new Utente("Andrea","Pepe", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);
        Utente u22 = new Utente("Matteo","Fanfarillo", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);
        Utente u23 = new Utente("Matteo","Ciccaglione", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);
        Utente u24 = new Utente("Vittoria","De Nitto", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u25 = new Utente("Valeria","Cardellini", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u26 = new Utente("Roberto","Monte", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u27 = new Utente("Giovanni","Saggio", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        Utente u28 = new Utente("Livia","Simoncini", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);
        Utente u29 = new Utente("Ludovico","Zarrelli", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);
        Utente u30 = new Utente("Alessandro","Montenegro", "******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.SPECIALIZZANDO);

        u6 = utenteDao.saveAndFlush(u6);
        u7 = utenteDao.saveAndFlush(u7);
        u1 = utenteDao.saveAndFlush(u1);
        u2 = utenteDao.saveAndFlush(u2);
        u3 = utenteDao.saveAndFlush(u3);
        u4 = utenteDao.saveAndFlush(u4);
        u5 = utenteDao.saveAndFlush(u5);
        u8 = utenteDao.saveAndFlush(u8);
        u9 = utenteDao.saveAndFlush(u9);
        u10 = utenteDao.saveAndFlush(u10);
        u11 = utenteDao.saveAndFlush(u11);
        u12 = utenteDao.saveAndFlush(u12);
        u13 = utenteDao.saveAndFlush(u13);
        u14 = utenteDao.saveAndFlush(u14);
        u15 = utenteDao.saveAndFlush(u15);
        u16 = utenteDao.saveAndFlush(u16);
        u17 = utenteDao.saveAndFlush(u17);
        u18 = utenteDao.saveAndFlush(u18);
        u19 = utenteDao.saveAndFlush(u19);
        u20 = utenteDao.saveAndFlush(u20);
        u21 = utenteDao.saveAndFlush(u20);
        u22 = utenteDao.saveAndFlush(u20);
        u23 = utenteDao.saveAndFlush(u20);
        u24 = utenteDao.saveAndFlush(u20);
        u25 = utenteDao.saveAndFlush(u20);
        u26 = utenteDao.saveAndFlush(u20);
        u27 = utenteDao.saveAndFlush(u20);
        u28 = utenteDao.saveAndFlush(u20);
        u29 = utenteDao.saveAndFlush(u20);
        u30 = utenteDao.saveAndFlush(u20);


        //creo servizi
        Servizio servizio1 = new Servizio("reparto");
        Servizio servizio2 = new Servizio("ambulatorio");
        Servizio servizio3 = new Servizio("pronto soccorso");


        servizioDao.save(servizio2);
        servizioDao.save(servizio1);
        servizioDao.save(servizio3);


        //Creo turni
        HashSet<CategoriaUtentiEnum> categorieVietate= new HashSet<>(Arrays.asList(
                CategoriaUtentiEnum.DONNA_INCINTA,
                CategoriaUtentiEnum.OVER_62,
                CategoriaUtentiEnum.IN_MALATTIA,
                CategoriaUtentiEnum.IN_FERIE)
        );


        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO, new HashSet<>(),false);
        t2.setNumUtentiGuardia(2);
        t2.setNumUtentiReperibilita(2);

        boolean giornoSuccessivo = true;
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, categorieVietate,giornoSuccessivo);
        t3.setNumUtentiGuardia(2);
        t3.setNumUtentiReperibilita(2);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, new HashSet<>(),false);
        t5.setNumUtentiGuardia(2);
        t5.setNumUtentiReperibilita(2);

        Turno t1 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio3, TipologiaTurno.POMERIDIANO, new HashSet<>(),false);
        t2.setNumUtentiGuardia(2);
        t2.setNumUtentiReperibilita(2);

        Turno t4 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio3, TipologiaTurno.NOTTURNO, categorieVietate,giornoSuccessivo);
        t3.setNumUtentiGuardia(2);
        t3.setNumUtentiReperibilita(2);

        turnoDao.saveAndFlush(t2);
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);
        turnoDao.saveAndFlush(t4);
        turnoDao.saveAndFlush(t1);

    }


    /** Metodo che server per caricare le festività dell'anno 2023/2024*/
    public void LoadHoliday() throws IOException {
        List<List<String>> data = new ArrayList<>();
        String currPath = System.getProperty("user.dir");
        //String filePath = currPath+"\\src\\main\\resources\\holiday.csv";
        String filePath = "";
        File file = new File("src/main/resources/holiday.csv");
        filePath = file.getAbsolutePath();

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