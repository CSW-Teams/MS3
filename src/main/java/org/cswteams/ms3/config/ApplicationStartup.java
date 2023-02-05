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
        Vincolo vincolo6 = new VincoloNumeroDiRuoloTurno();

        vincoloTurniContigui.setViolabile(true);
        vincolo1.setViolabile(true);

        vincolo1.setDescrizione("Vincolo Turno Persona: verifica che una determinata categoria non venga associata ad un turno proibito.");
        vincolo2.setDescrizione("Vincolo massimo periodo consecutivo. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        vincolo3.setDescrizione("Vincolo massimo periodo consecutivo per categoria over65.");
        vincolo4.setDescrizione("Vincolo massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        vincolo5.setDescrizione("Vincolo ubiquità. Verifica che lo stesso medico non venga assegnato contemporaneamente a due turni diversi nello stesso giorno");
        vincoloTurniContigui.setDescrizione("Vincolo turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");
        vincolo6.setDescrizione("Vincolo numero utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno");

        vincoloDao.saveAndFlush(vincoloTurniContigui);
        vincoloDao.saveAndFlush(vincolo1);
        vincoloDao.saveAndFlush(vincolo3);
        vincoloDao.saveAndFlush(vincolo2);
        vincoloDao.saveAndFlush(vincolo4);
        vincoloDao.saveAndFlush(vincolo5);
        vincoloDao.saveAndFlush(vincolo6);

    }
    
    private void registerHolidays(){
        /*
        try {
            LoadHoliday();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getMessage());
        }
        */

        // registra le domeniche nel ventennio 2021-2023 (ridurre overhead di comunicazione)
        // holidayController.registerSundays(LocalDate.of(2021, 1, 1), 3);

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
        CategoriaUtente ferie = new CategoriaUtente(categoriaFerie,LocalDate.now().minusDays(70), LocalDate.now().plusDays(7));
        categoriaUtenteDao.save(ferie);
        CategoriaUtente cardiologo = new CategoriaUtente(cardiologia,LocalDate.now().minusDays(70), LocalDate.now().plusDays(10000));
        categoriaUtenteDao.save(cardiologo);
        CategoriaUtente oncologo = new CategoriaUtente(oncologia,LocalDate.now().minusDays(70), LocalDate.now().plusDays(10000));
        categoriaUtenteDao.save(oncologo);


        CategoriaUtente repartoCardiologia = new CategoriaUtente(reparto_cardiologia, LocalDate.now().minusMonths(2),LocalDate.now().plusMonths(2));
        categoriaUtenteDao.save(repartoCardiologia);
        CategoriaUtente ambulatorioCardiologia = new CategoriaUtente(ambulatorio_cardiologia, LocalDate.now().minusMonths(2),LocalDate.now().plusMonths(2));
        categoriaUtenteDao.save(ambulatorioCardiologia);
        CategoriaUtente repartoOncologia = new CategoriaUtente(reparto_oncologia, LocalDate.now().minusMonths(2),LocalDate.now().plusMonths(2));
        categoriaUtenteDao.save(repartoOncologia);
        CategoriaUtente ambulatorioOncologia = new CategoriaUtente(ambulatorio_oncologia, LocalDate.now().minusMonths(2),LocalDate.now().plusMonths(2));
        categoriaUtenteDao.save(ambulatorioOncologia);

        //Creo utenti
        Utente u6 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1960, 3, 7),"giovannicantone@gmail.com", RuoloEnum.STRUTTURATO );
        u6.getStato().add(categoriaOver62);
        u6.getStato().add(ferie);
        // Aggiungo la specializzazione
        u6.getSpecializzazioni().add(cardiologo);
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", RuoloEnum.SPECIALIZZANDO );
        u1.getTurnazioni().add(repartoCardiologia);
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u2.getTurnazioni().add(repartoCardiologia);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", RuoloEnum.SPECIALIZZANDO);
        //u3.getTurnazioni().add(repartoCardiologia);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", RuoloEnum.STRUTTURATO);
        u4.getSpecializzazioni().add(cardiologo);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", RuoloEnum.STRUTTURATO);
        u5.getSpecializzazioni().add(oncologo);
        Utente u7 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello",RuoloEnum.STRUTTURATO);
        u7.getSpecializzazioni().add(oncologo);
        Utente u8 = new Utente("Manuel","Mastrofini", "MNLMASTR******", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", RuoloEnum.STRUTTURATO);
        u8.getSpecializzazioni().add(cardiologo);
        Utente u9 = new Utente("Giulia","Cantone", "GLCCTN******", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u9.getTurnazioni().add(repartoOncologia);
        Utente u10 = new Utente("Fabio","Valenzi", "FBVVLZ******", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u10.getTurnazioni().add(repartoOncologia);
        Utente u11 = new Utente("Giada","Rossi", "******", LocalDate.of(1997, 3, 14),"**@gmail.com", RuoloEnum.SPECIALIZZANDO );
        u11.getTurnazioni().add(repartoOncologia);
        Utente u12 = new Utente("Camilla","Verdi", "******", LocalDate.of(1997, 5, 23),"***@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u12.getTurnazioni().add(repartoCardiologia);
        Utente u13 = new Utente("Federica","Pollini", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u13.getTurnazioni().add(repartoCardiologia);
        Utente u14 = new Utente("Claudia","Rossi", "******", LocalDate.of(1982, 7, 6),"***@gmail.com@gmail.com", RuoloEnum.STRUTTURATO);
        u14.getSpecializzazioni().add(oncologo);
        Utente u15 = new Utente("Giorgio","Bianchi", "******", LocalDate.of(1993, 2, 12),"***@gmail.com@gmail.com", RuoloEnum.STRUTTURATO);
        u15.getSpecializzazioni().add(oncologo);
        Utente u16 = new Utente("Claudio","Gialli", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u16.getSpecializzazioni().add(cardiologo);
        Utente u17 = new Utente("Filippo","Neri", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u17.getTurnazioni().add(ambulatorioCardiologia);
        Utente u18 = new Utente("Vincenzo","Grassi", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u18.getSpecializzazioni().add(oncologo);
        Utente u19 = new Utente("Diana","Pasquali", "******", LocalDate.of(1998, 2, 12),"***@gmail.com@gmail.com@gmail.com", RuoloEnum.SPECIALIZZANDO);
        u19.getTurnazioni().add(ambulatorioCardiologia);
        Utente u20 = new Utente("Francesco","Lo Presti", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        Utente u21 = new Utente("Andrea","Pepe", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u21.getTurnazioni().add(ambulatorioOncologia);
        Utente u22 = new Utente("Matteo","Fanfarillo", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u22.getTurnazioni().add(repartoOncologia);
        Utente u23 = new Utente("Matteo","Ciccaglione", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u23.getTurnazioni().add(repartoOncologia);
        Utente u24 = new Utente("Vittoria","De Nitto", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u24.getSpecializzazioni().add(oncologo);
        Utente u25 = new Utente("Valeria","Cardellini", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u25.getSpecializzazioni().add(oncologo);
        Utente u26 = new Utente("Roberto","Monte", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u26.getSpecializzazioni().add(cardiologo);
        Utente u27 = new Utente("Giovanni","Saggio", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.STRUTTURATO);
        u27.getSpecializzazioni().add(cardiologo);
        Utente u28 = new Utente("Livia","Simoncini", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u28.getTurnazioni().add(repartoCardiologia);
        Utente u29 = new Utente("Ludovico","Zarrelli", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u29.getTurnazioni().add(repartoCardiologia);
        Utente u30 = new Utente("Alessandro","Montenegro", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u30.getTurnazioni().add(ambulatorioOncologia);
        Utente u31 = new Utente("Daniel","Lungu", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u31.getTurnazioni().add(repartoCardiologia);
        Utente u32 = new Utente("Andrea","Tosti", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u32.getTurnazioni().add(repartoCardiologia);
        Utente u33 = new Utente("Giorgio","Pesce", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u33.getTurnazioni().add(repartoOncologia);
        Utente u34 = new Utente("Valerio","Palmerini", "******", LocalDate.of(1998, 8, 12),"***@gmail.com@gmail.com",RuoloEnum.SPECIALIZZANDO);
        u34.getTurnazioni().add(repartoOncologia);

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
        u21 = utenteDao.saveAndFlush(u21);
        u22 = utenteDao.saveAndFlush(u22);
        u23 = utenteDao.saveAndFlush(u23);
        u24 = utenteDao.saveAndFlush(u24);
        u25 = utenteDao.saveAndFlush(u25);
        u26 = utenteDao.saveAndFlush(u26);
        u27 = utenteDao.saveAndFlush(u27);
        u28 = utenteDao.saveAndFlush(u28);
        u29 = utenteDao.saveAndFlush(u29);
        u30 = utenteDao.saveAndFlush(u30);
        u31 = utenteDao.saveAndFlush(u31);
        u32 = utenteDao.saveAndFlush(u32);
        u33 = utenteDao.saveAndFlush(u33);
        u34 = utenteDao.saveAndFlush(u34);


        //creo servizi
        Servizio servizio1 = new Servizio("cardiologia");
        Servizio servizio2 = new Servizio("oncologia");


        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO, MansioneEnum.GUARDIA, MansioneEnum.SALA_OPERATORIA));
        servizio2.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO, MansioneEnum.GUARDIA, MansioneEnum.SALA_OPERATORIA));

        servizioDao.save(servizio2);
        servizioDao.save(servizio1);

        Turno t1 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, MansioneEnum.GUARDIA, TipologiaTurno.POMERIDIANO,true);
        t1.setCategoryPolicies(Arrays.asList(
                new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(categoriaFerie, t1,  UserCategoryPolicyValue.EXCLUDE)
        ));

        Turno t2 = new Turno(LocalTime.of(14, 0), LocalTime.of(20, 0), servizio1, MansioneEnum.REPARTO, TipologiaTurno.POMERIDIANO,false);
        t2.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t2, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t2,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(reparto_cardiologia, t2, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t3 = new Turno(LocalTime.of(20, 0), LocalTime.of(8, 0), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO,false);
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaIncinta, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(reparto_cardiologia, t3, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t5 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio1, MansioneEnum.AMBULATORIO,TipologiaTurno.MATTUTINO, false);
        t5.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t5, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t5,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(cardiologia, t5, UserCategoryPolicyValue.INCLUDE),
            new UserCategoryPolicy(ambulatorio_cardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t6 = new Turno(LocalTime.of(10, 0), LocalTime.of(12, 0), servizio2, MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO, false);
        t6.setCategoryPolicies(Arrays.asList(
                new UserCategoryPolicy(categoriaMalattia, t6, UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(categoriaFerie, t6,  UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(oncologia, t6, UserCategoryPolicyValue.INCLUDE),
                new UserCategoryPolicy(ambulatorio_oncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));

        // Creazione del turno in sala operatoria in cardiologia ogni lunedì
        Turno salaOpCardio = new Turno(LocalTime.of(10, 0), LocalTime.of(23, 59), servizio1, MansioneEnum.SALA_OPERATORIA, TipologiaTurno.MATTUTINO, false);
        GiorniDellaSettimanaBitMask bitmask= new GiorniDellaSettimanaBitMask();
        bitmask.disableAllDays();
        salaOpCardio.setGiorniDiValidità(bitmask.addDayOfWeek(DayOfWeek.MONDAY));

        //Salvataggio dei Turni nel DB
        turnoDao.saveAndFlush(t1);
        turnoDao.saveAndFlush(t2);
        turnoDao.saveAndFlush(t3);
        turnoDao.saveAndFlush(t5);
        turnoDao.saveAndFlush(t6);
        turnoDao.saveAndFlush(salaOpCardio);

    }


    /** Metodo che server per caricare le festività dell'anno 2023/2024*/
    public void LoadHoliday() throws IOException {
        List<List<String>> data = new ArrayList<>();
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