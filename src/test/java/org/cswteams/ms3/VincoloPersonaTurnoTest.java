package org.cswteams.ms3;

import org.cswteams.ms3.entity.vincoli.ContestoVincolo;
import org.cswteams.ms3.entity.vincoli.VincoloCategorieUtenteTurno;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.TurnoException;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class VincoloPersonaTurnoTest {

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

    private final VincoloCategorieUtenteTurno vincoloPersonaTurno = new VincoloCategorieUtenteTurno();

    @Autowired
    private UserCategoryPolicyDao userCategoryPolicyDao;

    @Autowired
    private CategorieDao categorieDao;

    @Test(expected=ViolatedConstraintException.class)
    //**Test che verifica che una persona incinta non può essere aggiunta ad un turno notturno -
     //  violazione del vincolo categoria. */
    public void pregnancyTEST() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);
        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO));
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO, true);

        t1.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE)
            ));
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE)
            ));

        turnoDao.save(t1);
        turnoDao.save(t3);
                
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        //Aggiungi categoria all'utente
        CategoriaUtente incinta = new CategoriaUtente(categoriaIncinta, LocalDate.of(2023, 1, 4), LocalDate.of(2023, 10, 4));
        categoriaUtenteDao.saveAndFlush(incinta);
        //Crea utente - PERSONA INCINTA
        Utente pregUser = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO , AttoreEnum.UTENTE );
        pregUser.getStato().add(incinta);
        utenteDao.saveAndFlush(pregUser);
        UserScheduleState pregUserState = new UserScheduleState(pregUser, null);
        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(pregUserState,turnoNotturno));
    }

    @Test(expected=ViolatedConstraintException.class)
    //**Test che verifica che una persona over62 non può essere aggiunta ad un turno notturno
     //  violazione del vincolo categoria. *//*
    public void over62TEST() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);

        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO));
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1,MansioneEnum.REPARTO,TipologiaTurno.MATTUTINO, true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO,TipologiaTurno.NOTTURNO,  true);

        t1.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE)
            ));
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE)
            ));
        
        turnoDao.save(t1);
        turnoDao.save(t3);
                
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,null,null);
        assegnazioneTurnoDao.save(turnoNotturno);
        //Crea categoria
        CategoriaUtente over62 = new CategoriaUtente(categoriaOVER62,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(over62);
        //Crea utente - PERSONA OVER 62
        Utente over62user = new Utente("Giovanni","Cantone", "******", LocalDate.of(1960, 3, 14),"@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE  );
        over62user.getStato().add(over62);
        utenteDao.saveAndFlush(over62user);
        UserScheduleState over62userState = new UserScheduleState(over62user, null);
        //Verifica il vincolo
        //La persona over62 non può essere aggiunta ai turni  notturni, l'eccezioen deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(over62userState,turnoNotturno));
    }

    @Test(expected=ViolatedConstraintException.class)
    //**Test che verifica che una persona in malattia non può essere aggiunta ad un turno notturno
    //violazione del vincolo categoria. *//*
    public void InMalattiaTEST() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);

        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);
        
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO));
        servizioDao.save(servizio1);

        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO,true);

        t1.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE)
            ));
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE)
            ));

        turnoDao.save(t1);
        turnoDao.save(t3);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattutinoinmalattia = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        assegnazioneTurnoDao.save(turnoMattutinoinmalattia);
        //Crea categoria IN MALATTIA
        CategoriaUtente inmalattia = new CategoriaUtente(categoriaMalattia,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(inmalattia);
        //Crea utente - PERSONA IN MALATTIA
        Utente inmalattiauser = new Utente("Giovanni","Cantone", "******", LocalDate.of(1960, 3, 14),"@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE  );
        inmalattiauser.getStato().add(inmalattia);
        utenteDao.saveAndFlush(inmalattiauser);
        UserScheduleState inmalattiauserState = new UserScheduleState(inmalattiauser, null);
        //Verifica il vincolo
        //La persona in malattia non può essere aggiunta ai turni durante la malattia, quindi l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inmalattiauserState,turnoMattutinoinmalattia));
    }

    @Test(expected=ViolatedConstraintException.class)
    //**Test che verifica che una persona in ferie non può essere aggiunta ad alcun turno -
    // * violazione del vincolo categoria. *//*
    public void InFerieTEST() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);

        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.AMBULATORIO, MansioneEnum.REPARTO));

        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO, TipologiaTurno.NOTTURNO, true);

        t1.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE)
            ));
        t3.setCategoryPolicies(Arrays.asList(
            new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE),
            new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE)
            ));

        turnoDao.save(t1);
        turnoDao.save(t3);
        
        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattutinoiferie = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        assegnazioneTurnoDao.save(turnoMattutinoiferie);
        //Crea categoria IN MALATTIA
        CategoriaUtente inferie = new CategoriaUtente(categoriaFerie,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(inferie);
        //Crea utente - PERSONA IN FERIE
        Utente inferieuser = new Utente("Giovanni","Cantone", "******", LocalDate.of(1960, 3, 14),"@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE  );

        inferieuser.getStato().add(inferie);
        utenteDao.saveAndFlush(inferieuser);
        UserScheduleState inferieuserState = new UserScheduleState(inferieuser, null);
        //Verifica il vincolo
        //La persona in malattia non può essere aggiunta ai turni durante la malattia, quindi l'eccezione deve essere sollevata
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(inferieuserState,turnoMattutinoiferie));
    }

    @Test(expected=ViolatedConstraintException.class)
    /**Test che verifica che una persona in ferie non può essere aggiunta ad alcun turno -
     * violazione del vincolo categoria. */
    public void turnoInCardiologiaTest() throws ViolatedConstraintException, TurnoException {
        //Crea turni e servizio
        //CREA LE CATEGORIE DI TIPO SPECIALIZZAZIONE E TURNAZIONE (ESCLUSIVE PER I TURNI)
        Categoria cardiologia = new Categoria("CARDIOLOGIA", TipoCategoriaEnum.SPECIALIZZAZIONE);
        Categoria ambulatorioCardiologia = new Categoria("AMBULATORIO CARDIOLOGIA", TipoCategoriaEnum.TURNAZIONE);

        categorieDao.saveAndFlush(cardiologia);
        categorieDao.saveAndFlush(ambulatorioCardiologia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().addAll(Arrays.asList(MansioneEnum.REPARTO, MansioneEnum.AMBULATORIO));
        servizioDao.saveAndFlush(servizio1);
        Turno t1 = new Turno(LocalTime.of(10, 0), Duration.ofHours(2), servizio1, MansioneEnum.AMBULATORIO, TipologiaTurno.MATTUTINO, true);

        t1.setCategoryPolicies(Arrays.asList(
                new UserCategoryPolicy(cardiologia, t1, UserCategoryPolicyValue.INCLUDE),
                new UserCategoryPolicy(ambulatorioCardiologia, t1, UserCategoryPolicyValue.INCLUDE)
        ));

        turnoDao.saveAndFlush(t1);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoCardiologia = new AssegnazioneTurno(LocalDate.of(2023,1, 4),t1,null,null);
        assegnazioneTurnoDao.saveAndFlush(turnoCardiologia);

        CategoriaUtente cardiologo = new CategoriaUtente(cardiologia,LocalDate.of(2023, 1, 3), LocalDate.of(2100, 10, 4));
        categoriaUtenteDao.saveAndFlush(cardiologo);
        CategoriaUtente ambulatorioInCardiologia = new CategoriaUtente(ambulatorioCardiologia,LocalDate.of(2023, 1, 3), LocalDate.of(2023, 10, 4));
        categoriaUtenteDao.saveAndFlush(ambulatorioInCardiologia);
        Utente specializzandoCardiologia = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO,AttoreEnum.UTENTE  );
        specializzandoCardiologia.getTurnazioni().add(ambulatorioInCardiologia);
        utenteDao.saveAndFlush(specializzandoCardiologia);
        Utente specializzatoCardiologia = new Utente("Giacomo","Bianchi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com","", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE );
        specializzatoCardiologia.getSpecializzazioni().add(cardiologo);
        utenteDao.saveAndFlush(specializzatoCardiologia);
        UserScheduleState specializzato = new UserScheduleState(specializzatoCardiologia, null);
        UserScheduleState specializzando = new UserScheduleState(specializzandoCardiologia, null);

        //Verifica il vincolo
        try{
            vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(specializzato,turnoCardiologia));
            vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(specializzando,turnoCardiologia));
        }catch(ViolatedConstraintException ex){
            Assert.fail();
        }
        Utente utenteacaso = new Utente("Giovanni","Cantone", "******", LocalDate.of(1960, 3, 14),"@gmail.com", "passw", RuoloEnum.STRUTTURATO,AttoreEnum.UTENTE  );
        utenteDao.saveAndFlush(utenteacaso);
        UserScheduleState nonCardiologo = new UserScheduleState(utenteacaso, null);
        vincoloPersonaTurno.verificaVincolo(new ContestoVincolo(nonCardiologo, turnoCardiologia));


    }


}
