package org.cswteams.ms3;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt. It means that
@Transactional
public class VincoloMaxPeriodoConsecutivoTest {
/*    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private TurnoDao turnoDao;

    @Autowired
    private ServizioDao servizioDao;

    @Autowired
    private UserCategoryPolicyDao userCategoryPolicyDao;

    @Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    /*public void oreConsecutiveTEST() throws ViolatedConstraintException, TurnoException {
        //CREA LE CATEGORIE DI TIPO STATO (ESCLUSIVE PER I TURNI)
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);
        //Crea turni e servizio
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO, TipologiaTurno.MATTUTINO,true);
        Turno t2 = new Turno(LocalTime.of(14, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.POMERIDIANO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.NOTTURNO, true);
        //Turno t4 = new Turno(LocalTime.of(0, 0), LocalTime.of(8, 0), servizio1, TipologiaTurno.NOTTURNO, new HashSet<>(Arrays.asList(CategoriaUtentiEnum.DONNA_INCINTA, CategoriaUtentiEnum.OVER_62, CategoriaUtentiEnum.IN_MALATTIA, CategoriaUtentiEnum.IN_FERIE)));

        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaOVER62, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaIncinta, t1, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaMalattia, t2, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaFerie, t2, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaIncinta, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaOVER62, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaMalattia, t3, UserCategoryPolicyValue.EXCLUDE));
        userCategoryPolicyDao.save(new UserCategoryPolicy(categoriaFerie, t3, UserCategoryPolicyValue.EXCLUDE));

        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);
        //turnoDao.save(t4);
        //Crea utente
        Utente utente = new Utente("Martina","Salvati", "SLVMTN******", LocalDate.of(1997, 3, 14),"salvatimartina97@gmail.com", "passw", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE  );
        utenteDao.save(utente);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(utente)));
        assegnazioneTurnoDao.save(turnoPomeriggio);
        assegnazioneTurnoDao.save(turnoNotturno);

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Collections.singletonList(turnoPomeriggio)));

        UserScheduleState pregUserState = new UserScheduleState(utente, scheduleTest);

        Vincolo vincoloMaxOreConsecutive = new VincoloMaxPeriodoConsecutivo(12*60);
        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
        vincoloMaxOreConsecutive.verificaVincolo(new ContextConstraint(pregUserState,turnoNotturno));
    }*/

    //@Test(expected= ViolatedConstraintException.class)
    /**Test che verifica che un utente non può effettuare più di un tot ore consecutive */
    /*public void oreConsecutiveConCategoriaTEST() throws ViolatedConstraintException, TurnoException{
        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.save(servizio1);
        Turno t1 = new Turno(LocalTime.of(8, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.MATTUTINO,true);
        Turno t2 = new Turno(LocalTime.of(14, 0), Duration.ofHours(6), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.POMERIDIANO,true);
        Turno t3 = new Turno(LocalTime.of(20, 0), Duration.ofHours(12), servizio1, MansioneEnum.REPARTO,  TipologiaTurno.NOTTURNO,true);
        turnoDao.save(t1);
        turnoDao.save(t2);
        turnoDao.save(t3);

        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        //turnoDao.save(t4);
        CategoriaUtente over62 = new CategoriaUtente(categoriaOVER62,LocalDate.of(2023, 1, 4), LocalDate.of(2100, 10, 4));
        //Crea utente
        Utente over62user = new Utente("Stefano","Rossi", "STFRSS******", LocalDate.of(1953, 3, 14),"stfrss@gmail.com","", RuoloEnum.STRUTTURATO ,AttoreEnum.UTENTE );
        over62user.getStato().add(over62);

        //Aggiungi assegnazione turno
        AssegnazioneTurno turnoMattina = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t1,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));
        AssegnazioneTurno turnoPomeriggio = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t2,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));
        AssegnazioneTurno turnoNotturno = new AssegnazioneTurno(LocalDate.of(2023,1, 10),t3,new HashSet<>(),new HashSet<>(Collections.singletonList(over62user)));

        Schedule scheduleTest = new Schedule();
        scheduleTest.setAssegnazioniTurno(new ArrayList<>(Arrays.asList(turnoMattina,turnoNotturno)));

        UserScheduleState pregUserState = new UserScheduleState(over62user, scheduleTest);

      //  Vincolo vincoloMaxOreConsecutive = new VincoloMaxPeriodoConsecutivo(9*60, Collections.singletonList(categoriaOVER62));

        //La persona incinta non può essere aggiunta ai turni notturni, l'eccezione deve essere sollevata
       // vincoloMaxOreConsecutive.verificaVincolo(new ContextConstraint(pregUserState,turnoPomeriggio));
    }*/



}
