package org.cswteams.ms3.config;

import lombok.SneakyThrows;
import org.cswteams.ms3.control.preferenze.IHolidayController;
import org.cswteams.ms3.entity.category.*;
import org.cswteams.ms3.entity.doctor.*;
import org.cswteams.ms3.entity.policy.ConditionPolicy;
import org.cswteams.ms3.entity.policy.RotationPolicy;
import org.cswteams.ms3.entity.policy.SpecializationPolicy;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.entity.scocciature.ScocciaturaAssegnazioneUtente;
import org.cswteams.ms3.entity.scocciature.ScocciaturaDesiderata;
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
    private PermanentConditionDao permanentConditionDao;

    @Autowired
    private TemporaryConditionDAO temporaryConditionDao;

    @Autowired
    private RotationDao rotationDao;

    @Autowired
    private SpecializationDao specializationDao;

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
            //registerConstraints();
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
/*
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
    }
*/

    private void populateDB() throws TurnoException {

        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        // Condition may be structure specific TODO: Ask if it is needed a configuration file for that
        PermanentCondition over62 = new PermanentCondition("OVER_62");
        TemporaryCondition pregnant = new TemporaryCondition("INCINTA", LocalDate.now().toEpochDay(),LocalDate.now().plusMonths(9).toEpochDay());
        TemporaryCondition maternity = new TemporaryCondition("IN_MATERNITA'",LocalDate.now().toEpochDay(),LocalDate.now().plusDays(60).toEpochDay());
        TemporaryCondition vacation = new TemporaryCondition("IN_FERIE", LocalDate.now().toEpochDay(),LocalDate.now().plusDays(7).toEpochDay());
        TemporaryCondition sick = new TemporaryCondition("IN_MALATTIA", LocalDate.now().toEpochDay(),LocalDate.now().plusDays(7).toEpochDay());

        
        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE (INCLUSIVE)
        Specialization cardiologia = new Specialization("CARDIOLOGIA");
        Specialization oncologia = new Specialization("ONCOLOGIA");

        //CREA LA CATEGORIE DI TIPO TURNAZIONE (INCLUSIVE)
        Rotation repartoCardiologia = new Rotation("REPARTO CARDIOLOGIA");
        Rotation repartoOncologia = new Rotation("REPARTO ONCOLOGIA");
        Rotation ambulatorioCardiologia = new Rotation("AMBULATORIO CARDIOLOGIA");
        Rotation ambulatorioOncologia = new Rotation("AMBULATORIO ONCOLOGIA");

        // Save in persistence all possible conditions
        temporaryConditionDao.save(vacation);
        permanentConditionDao.save(over62);
        temporaryConditionDao.save(pregnant);
        temporaryConditionDao.save(sick);
        temporaryConditionDao.save(maternity);

        // Save in persistence all possible specialization
        specializationDao.save(cardiologia);
        specializationDao.save(oncologia);

        // Save in persistence all possible rotations
        rotationDao.save(repartoCardiologia);
        rotationDao.save(repartoOncologia);
        rotationDao.save(ambulatorioCardiologia);
        rotationDao.save(ambulatorioOncologia);


        //Creo utenti
        Doctor u6 = new Doctor("Giovanni","Cantone", "GVNCTN48M22D429*", LocalDate.of(1960, 3, 7),"giovannicantone@gmail.com", "passw", RuoloEnum.STRUTTURATO , AttoreEnum.PIANIFICATORE);
        u6.getConditions().add(over62);
        u6.getConditions().add(vacation);
        // Aggiungo la specializzazione
        u6.getSpecializations().add(cardiologia);
        Doctor u1 = new Doctor("Martina","Salvati", "SLVMTN97T56H501*", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.CONFIGURATORE);
        u1.getRotations().add(repartoCardiologia);
        Doctor u2 = new Doctor("Domenico","Verde", "VRDDMC96H16H501*", LocalDate.of(1997, 5, 23),"domenicoverde@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u2.getRotations().add(repartoCardiologia);
        Doctor u3 = new Doctor("Federica","Villani", "VLNFDR98P03H501*", LocalDate.of(1998, 2, 12),"federicavillani@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        //u3.getTurnazioni().add(repartoCardiologia);
        Doctor u4 = new Doctor("Daniele","Colavecchi", "CLVDNL82C21H501*", LocalDate.of(1982, 7, 6),"danielecolavecchi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u4.getSpecializations().add(cardiologia);
        Doctor u5 = new Doctor("Daniele","La Prova", "LPVDNL98R27H501*", LocalDate.of(1998, 2, 12),"danielelaprova@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u5.getSpecializations().add(oncologia);
        Doctor u7 = new Doctor("Luca","Fiscariello", "FSCLCU98L07B581*", LocalDate.of(1998, 8, 12),"lucafiscariello@gmail.com", "passw",RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u7.getSpecializations().add(oncologia);
        Doctor u8 = new Doctor("Manuel","Mastrofini", "MSTMNL80M20H501*", LocalDate.of(1988, 5, 4),"manuelmastrofini@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u8.getSpecializations().add(cardiologia);
        Doctor u9 = new Doctor("Giulia","Cantone", "CTNGLI78E44H501*", LocalDate.of(1991, 2, 12),"giuliacantone@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u9.getRotations().add(repartoOncologia);
        Doctor u10 = new Doctor("Fabio","Valenzi", "VLZFBA90A03H501*", LocalDate.of(1989, 12, 6),"fabiovalenzi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u10.getRotations().add(repartoOncologia);
        Doctor u11 = new Doctor("Giada","Rossi", "RSSGDI92H68H501*", LocalDate.of(1997, 3, 14),"giada.rossi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO ,AttoreEnum.UTENTE);
        u11.getRotations().add(repartoOncologia);
        Doctor u12 = new Doctor("Camilla","Verdi", "VRDCML95B41H501*", LocalDate.of(1997, 5, 23),"camilla.verdi@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u12.getRotations().add(repartoCardiologia);
        Doctor u13 = new Doctor("Federica","Pollini", "PLLFDR94S70H501*", LocalDate.of(1998, 2, 12),"federica.pollini@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u13.getRotations().add(repartoCardiologia);
        Doctor u14 = new Doctor("Claudia","Rossi", "RSSCLD91C52H501*", LocalDate.of(1982, 7, 6),"claudia.rossi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u14.getSpecializations().add(oncologia);
        Doctor u15 = new Doctor("Giorgio","Bianchi", "BNCGRG88E21H501*", LocalDate.of(1993, 2, 12),"giorgio.bianchi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u15.getSpecializations().add(oncologia);
        Doctor u16 = new Doctor("Claudio","Gialli", "GLLCLD89B14H501*", LocalDate.of(1998, 8, 12),"claudia.gialli@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u16.getSpecializations().add(cardiologia);
        Doctor u17 = new Doctor("Filippo","Neri", "NREFLP92R24H501*", LocalDate.of(1998, 2, 12),"filippo.neru@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u17.getRotations().add(ambulatorioCardiologia);
        Doctor u18 = new Doctor("Vincenzo","Grassi", "GRSVNC60A19H501*", LocalDate.of(1998, 8, 12),"vincenzo.grassi@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u18.getSpecializations().add(oncologia);
        Doctor u19 = new Doctor("Diana","Pasquali", "PSQDNI97D22H501*", LocalDate.of(1998, 2, 12),"diana.pasquali@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u19.getRotations().add(ambulatorioCardiologia);
        Doctor u20 = new Doctor("Francesco","Lo Presti", "LPSFRC66T05G071*", LocalDate.of(1998, 8, 12),"francesco.lopresti@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u20.getRotations().add(ambulatorioOncologia);
        Doctor u21 = new Doctor("Andrea","Pepe", "PPENDR99M05I150*", LocalDate.of(1998, 8, 12),"andrea.pepe@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u21.getRotations().add(ambulatorioOncologia);
        Doctor u22 = new Doctor("Matteo","Fanfarillo", "FNFMTT99E10A123E", LocalDate.of(1999, 9, 10),"matteo.fanfarillo99@gmail.com","passw", RuoloEnum.STRUTTURATO,AttoreEnum.PIANIFICATORE);
        u22.getRotations().add(repartoOncologia);
        Doctor u23 = new Doctor("Matteo","Ciccaglione", "CCCMTT99H15C439*", LocalDate.of(1998, 8, 12),"matteo.ciccaglione@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u23.getRotations().add(repartoOncologia);
        Doctor u24 = new Doctor("Vittoria","De Nitto", "DNTVTT60C59E612*", LocalDate.of(1998, 8, 12),"vittoria.denitto@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u24.getSpecializations().add(oncologia);
        Doctor u25 = new Doctor("Valeria","Cardellini", "CRDVLR68L44H501*", LocalDate.of(1998, 8, 12),"valeria.cardellini@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u25.getSpecializations().add(oncologia);
        Doctor u26 = new Doctor("Roberto","Monte", "MNTRBT69R01D666*", LocalDate.of(1998, 8, 12),"roberto.monte@gmail.com","passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u26.getSpecializations().add(cardiologia);
        Doctor u27 = new Doctor("Giovanni","Saggio", "SGGGVN65D30H501*", LocalDate.of(1998, 8, 12),"giovanni.saggio@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE);
        u27.getSpecializations().add(cardiologia);
        Doctor u28 = new Doctor("Livia","Simoncini", "SMNLVI98L17H501*", LocalDate.of(1998, 8, 12),"livia.simoncini@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u28.getRotations().add(repartoCardiologia);
        Doctor u29 = new Doctor("Ludovico","Zarrelli", "ZRRLDV99E03I370*", LocalDate.of(1998, 8, 12),"ludovico.zerrelli@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u29.getRotations().add(repartoCardiologia);
        Doctor u30 = new Doctor("Alessandro","Montenegro", "MNTLSS96P20H501*", LocalDate.of(1998, 8, 12),"alessandro.montenegro@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u30.getRotations().add(ambulatorioOncologia);
        Doctor u31 = new Doctor("Daniel","Lungu", "LNGDNL98T04H501*", LocalDate.of(1998, 8, 12),"daniel.lungu@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u31.getRotations().add(repartoCardiologia);
        Doctor u32 = new Doctor("Andrea","Tosti", "TSTNDR97A10H501*", LocalDate.of(1998, 8, 12),"andrea.tosti@gmail.com","passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u32.getRotations().add(repartoCardiologia);
        Doctor u33 = new Doctor("Giorgio","Pesce", "PSCGRG98E08H501*", LocalDate.of(1998, 8, 12),"giorgia.pesce@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u33.getRotations().add(repartoOncologia);
        Doctor u34 = new Doctor("Valerio","Palmerini", "PLMVLR93B12H501*", LocalDate.of(1998, 8, 12),"valerio.palmerini@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE);
        u34.getRotations().add(repartoOncologia);

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
        t1.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(sick, t1, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(vacation, t1,  UserCategoryPolicyValue.EXCLUDE)
        ));

        Turno t2 = new Turno(LocalTime.of(14, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.POMERIDIANO,false);
        t2.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(sick, t2, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(vacation, t2,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t2.setRotationPolicies(List.of(
                new RotationPolicy(repartoCardiologia, t2, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO,false);
        t3.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(sick, t3, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(vacation, t3,  UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(pregnant, t3,  UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(over62, t3,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t3.setRotationPolicies(List.of(
                new RotationPolicy(repartoCardiologia, t3, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t5 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio1, MansioneEnum.AMBULATORIO,TipologiaTurno.MATTUTINO, false);
        t5.setConditionPolicies(Arrays.asList(
            new ConditionPolicy(sick, t5, UserCategoryPolicyValue.EXCLUDE),
            new ConditionPolicy(vacation, t5,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t5.setSpecializationPolicies(List.of(
                new SpecializationPolicy(cardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));

        t5.setRotationPolicies(List.of(
                new RotationPolicy(ambulatorioCardiologia, t5, UserCategoryPolicyValue.INCLUDE)
        ));

        Turno t6 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio2, MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO, false);
        t6.setConditionPolicies(Arrays.asList(
                new ConditionPolicy(sick, t6, UserCategoryPolicyValue.EXCLUDE),
                new ConditionPolicy(vacation, t6,  UserCategoryPolicyValue.EXCLUDE)
        ));

        t6.setSpecializationPolicies(List.of(
                new SpecializationPolicy(oncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));

        t6.setRotationPolicies(List.of(
                new RotationPolicy(ambulatorioOncologia, t6, UserCategoryPolicyValue.INCLUDE)
        ));

        // Creazione del turno in sala operatoria in cardiologia ogni lunedì
        Turno salaOpCardio = new Turno(LocalTime.of(10, 0), Duration.ofHours(13).plusMinutes(59), servizio1, MansioneEnum.SALA_OPERATORIA, TipologiaTurno.MATTUTINO, false);
        GiorniDellaSettimanaBitMask bitmask = new GiorniDellaSettimanaBitMask();
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