package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.entity.scocciature.ScocciaturaAssegnazioneUtente;
import org.cswteams.ms3.entity.scocciature.ScocciaturaDesiderata;
import org.cswteams.ms3.entity.scocciature.ScocciaturaVacanza;
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

    @Autowired
    private ScocciaturaDao scocciaturaDao;
    @Autowired
    private DesiderataDao desiderataDao;

    @Autowired
    private ConfigVincoliDao configVincoliDao;

    @Autowired
    private ConfigVincoloMaxPeriodoConsecutivoDao configVincoloMaxPeriodoConsecutivoDao;


    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        
        /**
         * FIXME: sostiutire count con controllo su entità Config
         */
        if (utenteDao.count() == 0) {
            populateDB();
            registerConstraints();
            registerScocciature();
        }
        
    }

    private void registerScocciature() {
        int pesoDesiderata= 100;

        int pesoDomenicaPomeriggio=20;
        int pesoDomenicaMattina=20;
        int pesoSabatoNotte=20;

        int pesoSabatoPomeriggio=15;
        int pesoSabatoMattina=15;
        int pesoVenerdiNotte=15;
        int pesoDomenicaNotte=15;

        int pesoVenerdiPomeriggio=10;

        int pesoFerialeSemplice=5;
        int pesoFerialeNotturno=10;

        Scocciatura scocciaturaDomenicaMattina = new ScocciaturaAssegnazioneUtente(pesoDomenicaMattina,DayOfWeek.SUNDAY,TipologiaTurno.MATTUTINO);
        Scocciatura scocciaturaDomenicaPomeriggio = new ScocciaturaAssegnazioneUtente(pesoDomenicaPomeriggio,DayOfWeek.SUNDAY,TipologiaTurno.POMERIDIANO);
        Scocciatura scocciaturaDomenicaNotte = new ScocciaturaAssegnazioneUtente(pesoDomenicaNotte,DayOfWeek.SUNDAY,TipologiaTurno.NOTTURNO);

        Scocciatura scocciaturaSabatoMattina = new ScocciaturaAssegnazioneUtente(pesoSabatoMattina,DayOfWeek.SATURDAY,TipologiaTurno.MATTUTINO);
        Scocciatura scocciaturaSabatoPomeriggio = new ScocciaturaAssegnazioneUtente(pesoSabatoPomeriggio,DayOfWeek.SATURDAY,TipologiaTurno.POMERIDIANO);
        Scocciatura scocciaturaSabatoNotte = new ScocciaturaAssegnazioneUtente(pesoSabatoNotte,DayOfWeek.SATURDAY,TipologiaTurno.NOTTURNO);

        Scocciatura scocciaturaVenerdiPomeriggio = new ScocciaturaAssegnazioneUtente(pesoVenerdiPomeriggio,DayOfWeek.FRIDAY,TipologiaTurno.POMERIDIANO);
        Scocciatura scocciaturaVenerdiNotte = new ScocciaturaAssegnazioneUtente(pesoVenerdiNotte,DayOfWeek.FRIDAY,TipologiaTurno.NOTTURNO);

        Scocciatura scocciaturaDesiderata = new ScocciaturaDesiderata(pesoDesiderata);

        scocciaturaDao.save(scocciaturaDomenicaPomeriggio);
        scocciaturaDao.save(scocciaturaDomenicaMattina);
        scocciaturaDao.save(scocciaturaDomenicaNotte);

        scocciaturaDao.save(scocciaturaSabatoMattina);
        scocciaturaDao.save(scocciaturaSabatoPomeriggio);
        scocciaturaDao.save(scocciaturaSabatoNotte);

        scocciaturaDao.save(scocciaturaVenerdiPomeriggio);
        scocciaturaDao.save(scocciaturaVenerdiNotte);

        scocciaturaDao.save(scocciaturaDesiderata);

        List<DayOfWeek> giorniFeriali = Arrays.asList(DayOfWeek.MONDAY,DayOfWeek.TUESDAY,DayOfWeek.WEDNESDAY,DayOfWeek.THURSDAY,DayOfWeek.FRIDAY);
        for(DayOfWeek giornoFeriale: giorniFeriali){
            ScocciaturaAssegnazioneUtente scocciaturaFerialeMattina = new ScocciaturaAssegnazioneUtente(pesoFerialeSemplice,giornoFeriale,TipologiaTurno.MATTUTINO);
            scocciaturaDao.save(scocciaturaFerialeMattina);
            if(giornoFeriale != DayOfWeek.FRIDAY){
                ScocciaturaAssegnazioneUtente scocciaturaFerialePomeriggio = new ScocciaturaAssegnazioneUtente(pesoFerialeSemplice,giornoFeriale,TipologiaTurno.POMERIDIANO);
                scocciaturaDao.save(scocciaturaFerialePomeriggio);
                ScocciaturaAssegnazioneUtente scocciaturaFerialeNotturno = new ScocciaturaAssegnazioneUtente(pesoFerialeNotturno,giornoFeriale,TipologiaTurno.NOTTURNO);
                scocciaturaDao.save(scocciaturaFerialeNotturno);
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

            ConfigVincoloMaxPeriodoConsecutivo confOver62 = new ConfigVincoloMaxPeriodoConsecutivo(categoriaDao.findAllByNome("OVER_62"),Integer.parseInt(prop.getProperty("numMaxOreConsecutiveOver62"))*60);
            ConfigVincoloMaxPeriodoConsecutivo confIncinta = new ConfigVincoloMaxPeriodoConsecutivo(categoriaDao.findAllByNome("INCINTA"),Integer.parseInt(prop.getProperty("numMaxOreConsecutiveDonneIncinta"))*60);
            configVincoloMaxPeriodoConsecutivoDao.saveAndFlush(confOver62);
            configVincoloMaxPeriodoConsecutivoDao.saveAndFlush(confIncinta);
            configVincoli = new ConfigVincoli(
                    Integer.parseInt(prop.getProperty("numGiorniPeriodo")),
                    Integer.parseInt(prop.getProperty("maxOrePeriodo")) * 60,
                    Integer.parseInt(prop.getProperty("HorizonTurnoNotturno")),
                    Integer.parseInt(prop.getProperty("numMaxOreConsecutivePerTutti")) * 60,
                    Arrays.asList(confOver62,confIncinta)
            );
            configVincoliDao.save(configVincoli);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // nessun turno può essere allocato a questa persona durante il suo smonto notte
        VincoloTipologieTurniContigue vincoloTurniContigui = new VincoloTipologieTurniContigue(
            configVincoli.getHorizonTurnoNotturno(),
            ChronoUnit.HOURS,
            TipologiaTurno.NOTTURNO,
            new HashSet<>(Arrays.asList(TipologiaTurno.values()))
            );


        Vincolo vincolo1 = new VincoloCategorieUtenteTurno();
        Vincolo vincolo2 = new VincoloMaxPeriodoConsecutivo(configVincoli.getNumMaxMinutiConsecutiviPerTutti());
        Vincolo vincolo4 = new VincoloMaxOrePeriodo(configVincoli.getNumGiorniPeriodo(), configVincoli.getMaxMinutiPeriodo());
        Vincolo vincolo5 = new VincoloUbiquità();
        Vincolo vincolo6 = new VincoloNumeroDiRuoloTurno();

        vincoloTurniContigui.setViolabile(true);
        vincolo1.setViolabile(true);

        for(ConfigVincoloMaxPeriodoConsecutivo config : configVincoli.getConfigVincoloMaxPeriodoConsecutivoPerCategoria()){
            Vincolo vincolo = new VincoloMaxPeriodoConsecutivo(config.getNumMaxMinutiConsecutivi(), config.getCategoriaVincolata());
            vincolo.setDescrizione("Vincolo massimo periodo consecutivo per categoria "+config.getCategoriaVincolata().getNome());
            vincoloDao.saveAndFlush(vincolo);
        }
        vincolo1.setDescrizione("Vincolo Turno Persona: verifica che una determinata categoria non venga associata ad un turno proibito.");
        vincolo2.setDescrizione("Vincolo massimo periodo consecutivo. Verifica che un medico non lavori più di tot ore consecutive in una giornata.");
        vincolo4.setDescrizione("Vincolo massimo ore lavorative in un certo intervallo di tempo. Verifica che un medico non lavori più di tot ore in un arco temporale configurabile.");
        vincolo5.setDescrizione("Vincolo ubiquità. Verifica che lo stesso medico non venga assegnato contemporaneamente a due turni diversi nello stesso giorno");
        vincoloTurniContigui.setDescrizione("Vincolo turni contigui. Verifica se alcune tipologie possono essere assegnate in modo contiguo.");
        vincolo6.setDescrizione("Vincolo numero utenti per ruolo. Definisce quanti utenti di ogni ruolo devono essere associati ad ogni turno");

        vincoloDao.saveAndFlush(vincoloTurniContigui);
        vincoloDao.saveAndFlush(vincolo1);
        vincoloDao.saveAndFlush(vincolo2);
        vincoloDao.saveAndFlush(vincolo4);
        vincoloDao.saveAndFlush(vincolo5);
        vincoloDao.saveAndFlush(vincolo6);

        List<Vincolo> vincoli = vincoloDao.findByType("VincoloMaxPeriodoConsecutivo");
        System.out.println("ciao");
    }


    private void populateDB() throws TurnoException {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);

        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE (INCLUSIVE)
        Categoria cardiologia = new Categoria("CARDIOLOGIA", TipoCategoriaEnum.SPECIALIZZAZIONE);
        Categoria oncologia = new Categoria("ONCOLOGIA", TipoCategoriaEnum.SPECIALIZZAZIONE);

        //CREA LA CATEGORIE DI TIPO TURNAZIONE (INCLUSIVE)
        Categoria reparto_cardiologia = new Categoria("REPARTO CARDIOLOGIA", TipoCategoriaEnum.TURNAZIONE);
        Categoria reparto_oncologia = new Categoria("REPARTO ONCOLOGIA", TipoCategoriaEnum.TURNAZIONE);
        Categoria ambulatorio_cardiologia = new Categoria("AMBULATORIO CARDIOLOGIA", TipoCategoriaEnum.TURNAZIONE);
        Categoria ambulatorio_oncologia = new Categoria("AMBULATORIO ONCOLOGIA", TipoCategoriaEnum.TURNAZIONE);

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
        Utente u6 = new Utente("Giovanni","Cantone", "GVNTCT******", LocalDate.of(1960, 3, 7),"giovannicantone@gmail.com", "passw", RuoloEnum.STRUTTURATO , AttoreEnum.PIANIFICATORE);
        u6.getStato().add(categoriaOver62);
        u6.getStato().add(ferie);
        // Aggiungo la specializzazione
        u6.getSpecializzazioni().add(cardiologo);
        Utente u1 = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.CONFIGURATORE);
        u1.getTurnazioni().add(repartoCardiologia);
        Utente u2 = new Utente("Domenico","Verde", "DMNCVRD******", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u2.getTurnazioni().add(repartoCardiologia);
        Utente u3 = new Utente("Federica","Villani", "FDRVLLN******", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        //u3.getTurnazioni().add(repartoCardiologia);
        Utente u4 = new Utente("Daniele","Colavecchi", "DNLCLV******", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u4.getSpecializzazioni().add(cardiologo);
        Utente u5 = new Utente("Daniele","La Prova", "DNLLPRV******", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u5.getSpecializzazioni().add(oncologo);
        Utente u7 = new Utente("Luca","Fiscariello", "FSCRLC******", LocalDate.of(1998, 8, 12),"lucafiscariello@gmail.com", "passw",RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u7.getSpecializzazioni().add(oncologo);
        Utente u8 = new Utente("Manuel","Mastrofini", "MNLMASTR******", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u8.getSpecializzazioni().add(cardiologo);
        Utente u9 = new Utente("Giulia","Cantone", "GLCCTN******", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u9.getTurnazioni().add(repartoOncologia);
        Utente u10 = new Utente("Fabio","Valenzi", "FBVVLZ******", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u10.getTurnazioni().add(repartoOncologia);
        Utente u11 = new Utente("Giada","Rossi", "******", LocalDate.of(1997, 3, 14),"giada.rossi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO ,AttoreEnum.UTENTE);
        u11.getTurnazioni().add(repartoOncologia);
        Utente u12 = new Utente("Camilla","Verdi", "******", LocalDate.of(1997, 5, 23),"camilla.verdi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u12.getTurnazioni().add(repartoCardiologia);
        Utente u13 = new Utente("Federica","Pollini", "******", LocalDate.of(1998, 2, 12),"federica.pollini@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u13.getTurnazioni().add(repartoCardiologia);
        Utente u14 = new Utente("Claudia","Rossi", "******", LocalDate.of(1982, 7, 6),"claudia.rossi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u14.getSpecializzazioni().add(oncologo);
        Utente u15 = new Utente("Giorgio","Bianchi", "******", LocalDate.of(1993, 2, 12),"giorgio.bianchi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u15.getSpecializzazioni().add(oncologo);
        Utente u16 = new Utente("Claudio","Gialli", "******", LocalDate.of(1998, 8, 12),"claudia.gialli@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u16.getSpecializzazioni().add(cardiologo);
        Utente u17 = new Utente("Filippo","Neri", "******", LocalDate.of(1998, 2, 12),"filippo.neru@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u17.getTurnazioni().add(ambulatorioCardiologia);
        Utente u18 = new Utente("Vincenzo","Grassi", "******", LocalDate.of(1998, 8, 12),"vincenzo.grassi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u18.getSpecializzazioni().add(oncologo);
        Utente u19 = new Utente("Diana","Pasquali", "******", LocalDate.of(1998, 2, 12),"diana.pasquali@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u19.getTurnazioni().add(ambulatorioCardiologia);
        Utente u20 = new Utente("Francesco","Lo Presti", "******", LocalDate.of(1998, 8, 12),"francesco.lopresti@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u20.getTurnazioni().add(ambulatorioOncologia);
        Utente u21 = new Utente("Andrea","Pepe", "******", LocalDate.of(1998, 8, 12),"andrea.pepe@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u21.getTurnazioni().add(ambulatorioOncologia);
        Utente u22 = new Utente("Matteo","Fanfarillo", "******", LocalDate.of(1999, 9, 10),"matteo.fanfarillo@gmail.com","fanfa", RuoloEnum.STRUTTURATO,AttoreEnum.PIANIFICATORE);
        u22.getTurnazioni().add(repartoOncologia);
        Utente u23 = new Utente("Matteo","Ciccaglione", "******", LocalDate.of(1998, 8, 12),"matteo.ciccaglione@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u23.getTurnazioni().add(repartoOncologia);
        Utente u24 = new Utente("Vittoria","De Nitto", "******", LocalDate.of(1998, 8, 12),"vittoria.denitto@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u24.getSpecializzazioni().add(oncologo);
        Utente u25 = new Utente("Valeria","Cardellini", "******", LocalDate.of(1998, 8, 12),"valeria.cardellini@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u25.getSpecializzazioni().add(oncologo);
        Utente u26 = new Utente("Roberto","Monte", "******", LocalDate.of(1998, 8, 12),"roberto.monte@gmail.com","passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u26.getSpecializzazioni().add(cardiologo);
        Utente u27 = new Utente("Giovanni","Saggio", "******", LocalDate.of(1998, 8, 12),"giovanni.saggio@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u27.getSpecializzazioni().add(cardiologo);
        Utente u28 = new Utente("Livia","Simoncini", "******", LocalDate.of(1998, 8, 12),"livia.simoncini@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u28.getTurnazioni().add(repartoCardiologia);
        Utente u29 = new Utente("Ludovico","Zarrelli", "******", LocalDate.of(1998, 8, 12),"ludovico.zerrelli@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u29.getTurnazioni().add(repartoCardiologia);
        Utente u30 = new Utente("Alessandro","Montenegro", "******", LocalDate.of(1998, 8, 12),"alessandro.montenegro@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u30.getTurnazioni().add(ambulatorioOncologia);
        Utente u31 = new Utente("Daniel","Lungu", "******", LocalDate.of(1998, 8, 12),"daniel.lungu@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u31.getTurnazioni().add(repartoCardiologia);
        Utente u32 = new Utente("Andrea","Tosti", "******", LocalDate.of(1998, 8, 12),"andrea.tosti@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u32.getTurnazioni().add(repartoCardiologia);
        Utente u33 = new Utente("Giorgio","Pesce", "******", LocalDate.of(1998, 8, 12),"giorgia.pesce@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u33.getTurnazioni().add(repartoOncologia);
        Utente u34 = new Utente("Valerio","Palmerini", "******", LocalDate.of(1998, 8, 12),"valerio.palmerini@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
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

        Turno t1 = new Turno(LocalTime.of(14, 0), Duration.ofHours(8), servizio1, MansioneEnum.GUARDIA, TipologiaTurno.POMERIDIANO,true);
        t1.setCategoryPolicies(Arrays.asList(
                new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(categoriaFerie, t1,  UserCategoryPolicyValue.EXCLUDE)
        ));

        Turno t2 = new Turno(LocalTime.of(14, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.POMERIDIANO,false);
        t2.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t2, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t2,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(reparto_cardiologia, t2, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO,false);
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaIncinta, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(reparto_cardiologia, t3, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t5 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio1, MansioneEnum.AMBULATORIO,TipologiaTurno.MATTUTINO, false);
        t5.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t5, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t5,  UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(cardiologia, t5, UserCategoryPolicyValue.INCLUDE),
            new UserCategoryPolicy(ambulatorio_cardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t6 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio2, MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO, false);
        t6.setCategoryPolicies(Arrays.asList(
                new UserCategoryPolicy(categoriaMalattia, t6, UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(categoriaFerie, t6,  UserCategoryPolicyValue.EXCLUDE),
                new UserCategoryPolicy(oncologia, t6, UserCategoryPolicyValue.INCLUDE),
                new UserCategoryPolicy(ambulatorio_oncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));

        // Creazione del turno in sala operatoria in cardiologia ogni lunedì
        Turno salaOpCardio = new Turno(LocalTime.of(10, 0), Duration.ofHours(13).plusMinutes(59), servizio1, MansioneEnum.SALA_OPERATORIA, TipologiaTurno.MATTUTINO, false);
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

        Desiderata desiderata = new Desiderata(LocalDate.of(2023,3,12),new ArrayList<>(), u3);
        u3.getDesiderataList().add(desiderata);

        desiderataDao.save(desiderata);
        utenteDao.saveAndFlush(u3);


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