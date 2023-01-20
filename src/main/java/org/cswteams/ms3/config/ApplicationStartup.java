package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.entity.vincoli.VincoloMaxOrePeriodo;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.dto.HolidayDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.entity.vincoli.*;
import org.cswteams.ms3.enums.*;
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
    private CategorieDao categoriaDao;

    @Autowired
    private IHolidayController holidayController;

    @Autowired
    private ScheduleDao dao;

    @Autowired
    private VincoloDao vincoloDao;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        populateDB();
        registerHolidays();
        registerConstraints();
        //populateDBTestSchedule();
    }

    private void registerConstraints(){

        //Creo vincoli
        final int massimoPeriodoContiguo = 12*60;
        final int numGiorni = 7;
        final int numMaxMinuti = 80*60;
        final int massimoPeriodoContiguoOver62 = 6*60;


        // nessun turno può essere allocato a questa persona durante il suo smonto notte
        VincoloTipologieTurniContigue vincoloTurniContigui = new VincoloTipologieTurniContigue(
            20,
            ChronoUnit.HOURS,
            TipologiaTurno.NOTTURNO,
            new HashSet<>(Arrays.asList(TipologiaTurno.values()))
            );


        Vincolo vincolo1 = new VincoloCategorieUtenteTurno();
        Vincolo vincolo2 = new VincoloMaxPeriodoConsecutivo(massimoPeriodoContiguo,categoriaDao.findAll());
        Vincolo vincolo3 = new VincoloMaxPeriodoConsecutivo(massimoPeriodoContiguoOver62,Arrays.asList(categoriaDao.findAllByNome("OVER_62")));
        Vincolo vincolo4 = new VincoloMaxOrePeriodo(numGiorni,numMaxMinuti);
        Vincolo vincolo5 = new VincoloUbiquità();

        vincoloTurniContigui.setViolabile(true);
        vincolo2.setViolabile(true);

        vincolo1.setDescrizione("Vincolo Turno Persona: verifica che una determinata categoria non venga associata ad un turno proibito.");
        vincolo2.setDescrizione("Vincolo massimo ore lavorative continuative. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        vincolo3.setDescrizione("Vincolo massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        vincoloTurniContigui.setDescrizione("Vincolo turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");

        vincoloDao.saveAndFlush(vincoloTurniContigui);
        vincoloDao.saveAndFlush(vincolo1);
        vincoloDao.saveAndFlush(vincolo3);
        vincoloDao.saveAndFlush(vincolo2);
        vincoloDao.saveAndFlush(vincolo4);
        vincoloDao.saveAndFlush(vincolo5);
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


    private void populateDB() throws TurnoException {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", 0);
        Categoria categoriaIncinta = new Categoria("INCINTA", 0);
        Categoria categoriaFerie = new Categoria("IN_FERIE", 0);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", 0);
        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE (INCLUSIVE)
        Categoria cardiologia = new Categoria("CARDIOLOGIA", 1);
        Categoria oncologia = new Categoria("ONCOLOGIA", 1);
        //CREA LA CATEGORIE DI TIPO TURNAZIONE (INCLUSIVE)
        Categoria reparto_cardiologia = new Categoria("REPARTO CARDIOLOGIA", 2);
        Categoria reparto_oncologia = new Categoria("REPARTO ONCOLOGIA", 2);
        Categoria ambulatorio_cardiologia = new Categoria("AMBULATORIO CARDIOLOGIA", 2);
        Categoria ambulatorio_oncologia = new Categoria("AMBULATORIO ONCOLOGIA", 2);

        categoriaDao.save(categoriaFerie);
        categoriaDao.save(categoriaOVER62);
        categoriaDao.save(categoriaIncinta);
        categoriaDao.save(categoriaMalattia);
        categoriaDao.save(cardiologia);
        categoriaDao.save(oncologia);
        categoriaDao.save(reparto_cardiologia);
        categoriaDao.save(reparto_oncologia);
        categoriaDao.save(ambulatorio_cardiologia);
        categoriaDao.save(ambulatorio_oncologia);


        //Creo categorie stato per un utente specifico
        CategoriaUtente categoriaOver62 = new CategoriaUtente(categoriaOVER62,LocalDate.of(2022,3,7), LocalDate.now().plusDays(1000));
        categoriaUtenteDao.save(categoriaOver62);
        CategoriaUtente ferie = new CategoriaUtente(categoriaFerie,LocalDate.now(), LocalDate.now().plusDays(7));
        categoriaUtenteDao.save(ferie);
        CategoriaUtente cardiologo = new CategoriaUtente(cardiologia,LocalDate.now(), LocalDate.now().plusDays(10000));
        categoriaUtenteDao.save(cardiologo);

        //Creo utenti
        Utente u6 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1960, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );
        u6.getStato().add(categoriaOver62);
        u6.getStato().add(ferie);
        // Aggiungo la specializzazione
        u6.getSpecializzazioni().add(cardiologo);
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );

        CategoriaUtente categoria_cardiologia = new CategoriaUtente(reparto_cardiologia, LocalDate.now(),LocalDate.now().plusMonths(2));
        categoriaUtenteDao.save(categoria_cardiologia);

        u1.getTurnazioni().add(categoria_cardiologia);
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u2.getTurnazioni().add(categoria_cardiologia);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u3.getTurnazioni().add(categoria_cardiologia);
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
        Utente u16 = new Utente("Claudio","Gialli", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u17 = new Utente("Filippo","Neri", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u18 = new Utente("Vincenzo","Grassi", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u19 = new Utente("Diana","Pasquali", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        Utente u20 = new Utente("Francesco","Lo Presti", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u21 = new Utente("Andrea","Pepe", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        Utente u22 = new Utente("Matteo","Fanfarillo", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        Utente u23 = new Utente("Matteo","Ciccaglione", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        Utente u24 = new Utente("Vittoria","De Nitto", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u25 = new Utente("Valeria","Cardellini", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u26 = new Utente("Roberto","Monte", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u27 = new Utente("Giovanni","Saggio", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u28 = new Utente("Livia","Simoncini", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        Utente u29 = new Utente("Ludovico","Zarrelli", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        Utente u30 = new Utente("Alessandro","Montenegro", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);

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
        Servizio servizio2 = new Servizio("gastroenterologia");
        Servizio servizio3 = new Servizio("allergologia");

        servizio1.getMansioni().add(MansioneEnum.AMBULATORIO);

        servizioDao.save(servizio2);
        servizioDao.save(servizio1);
        servizioDao.save(servizio3);

        //Creo turni
        HashSet<Categoria> categorieVietate= new HashSet<>(Arrays.asList(
                categoriaIncinta,
                categoriaOVER62,
                categoriaMalattia,
                categoriaFerie)
        );

        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, TipologiaTurno.POMERIDIANO,false);
        t2.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t2, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t2,  UserCategoryPolicyValue.EXCLUDE)
        ));
        t2.setNumUtentiGuardia(2);
        t2.setNumUtentiReperibilita(2);

        boolean giornoSuccessivo = true;
        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO,giornoSuccessivo);
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaIncinta, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3,  UserCategoryPolicyValue.EXCLUDE)
        ));
        t3.setCategorieVietate(categorieVietate);
        t3.setNumUtentiGuardia(2);
        t3.setNumUtentiReperibilita(2);

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, TipologiaTurno.MATTUTINO, false);
        t5.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t5, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t5,  UserCategoryPolicyValue.EXCLUDE)
        ));
        t5.setNumUtentiGuardia(2);
        t5.setNumUtentiReperibilita(2);


        turnoDao.saveAndFlush(t2);
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);


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