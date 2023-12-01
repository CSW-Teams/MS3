package org.cswteams.ms3.control.controllerscheduler.utils;

import org.cswteams.ms3.dao.*;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.*;
import org.cswteams.ms3.exception.TurnoException;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TestEnv extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    protected CategorieDao categorieDao;
    @Autowired
    protected CategoriaUtenteDao categoriaUtenteDao;
    @Autowired
    protected UtenteDao utenteDao;
    @Autowired
    protected TurnoDao turnoDao;
    @Autowired
    protected UserCategoryPolicyDao userCategoryPolicyDao;
    @Autowired
    protected ServizioDao servizioDao;

    @Before
    public void populateDBTestSchedule() throws TurnoException {
        Categoria categoriaOVER62 = new Categoria("OVER_62", TipoCategoriaEnum.STATO);
        Categoria categoriaIncinta = new Categoria("INCINTA", TipoCategoriaEnum.STATO);
        Categoria categoriaFerie = new Categoria("IN_FERIE", TipoCategoriaEnum.STATO);
        Categoria categoriaMalattia = new Categoria("IN_MALATTIA", TipoCategoriaEnum.STATO);

        categorieDao.saveAndFlush(categoriaOVER62);
        categorieDao.saveAndFlush(categoriaIncinta);
        categorieDao.saveAndFlush(categoriaFerie);
        categorieDao.saveAndFlush(categoriaMalattia);

        Servizio servizio1 = new Servizio("cardiologia");
        servizio1.getMansioni().add(MansioneEnum.REPARTO);
        servizioDao.saveAndFlush(servizio1);
        //Creo utente con categoria incinta
        Utente pregUser = new Utente("Giulia", "Rossi", "GLRRSS******", LocalDate.of(1954, 3, 14), "glrss@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
        CategoriaUtente ci = new CategoriaUtente(categoriaIncinta, LocalDate.now(), LocalDate.now().plusDays(10));
        categoriaUtenteDao.saveAndFlush(ci);
        pregUser.getStato().add(ci);
        utenteDao.saveAndFlush(pregUser);
        //Creo utente generico
        Utente utente = new Utente("Manuel", "Rossi", "******", LocalDate.of(1997, 3, 14), "salvatimartina97@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
        utenteDao.saveAndFlush(utente);
        //Crea turni e servizio
        servizioDao.saveAndFlush(servizio1);
        Turno t1 = new Turno(
                LocalTime.of(20, 0),
                Duration.ofHours(12),
                servizio1,
                MansioneEnum.REPARTO,
                TipologiaTurno.NOTTURNO,
                true
        );
        UserCategoryPolicy ucp1 = new UserCategoryPolicy(categoriaIncinta, t1, UserCategoryPolicyValue.EXCLUDE);
        UserCategoryPolicy ucp2 = new UserCategoryPolicy(categoriaOVER62, t1, UserCategoryPolicyValue.EXCLUDE);
        UserCategoryPolicy ucp3 = new UserCategoryPolicy(categoriaFerie, t1, UserCategoryPolicyValue.EXCLUDE);
        UserCategoryPolicy ucp4 = new UserCategoryPolicy(categoriaMalattia, t1, UserCategoryPolicyValue.EXCLUDE);

        //------------------------
        List<UserCategoryPolicy> cplist = new ArrayList<>();
        cplist.add(ucp1);
        cplist.add(ucp2);
        cplist.add(ucp3);
        cplist.add(ucp4);
        t1.setCategoryPolicies(cplist);
        //------------------------

        turnoDao.saveAndFlush(t1);
        userCategoryPolicyDao.saveAndFlush(ucp1);
        userCategoryPolicyDao.saveAndFlush(ucp2);
        userCategoryPolicyDao.saveAndFlush(ucp3);
        userCategoryPolicyDao.saveAndFlush(ucp4);
    }
}
