//package org.cswteams.ms3.control.preferences;
//
//import org.cswteams.ms3.dao.PreferenceDAO;
//import org.cswteams.ms3.dao.UserDAO;
//import org.cswteams.ms3.dto.preferences.*;
//import org.cswteams.ms3.entity.Preference;
//import org.cswteams.ms3.entity.User;
//import org.cswteams.ms3.exception.DatabaseException;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Profile;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import javax.transaction.Transactional;
//import javax.validation.ValidationException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
//@Profile("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // The spring application context will be considered "dirty" before each test method, and will be rebuilt.
//public class ControllerDesiderataTest {
//
//    @Autowired
//    private IPreferenceController controllerDesiderata;
//
//    @Autowired
//    private UserDAO utenteDao;
//
//    @Autowired
//    private PreferenceDAO preferenceDao;
//
//    @Test
//    @Transactional
//    public void testAggiungiDesiderata1() throws DatabaseException {
//        //VARIABILI UTILI
//        Preference desiderata;
//
//        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
//        User u = utenteDao.findByEmail("domenicoverde@gmail.com");
//        long utenteId = u.getId();
//        PreferenceDTOIn inDto = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceInWithUIDDTO uuidDto = new PreferenceInWithUIDDTO(utenteId, inDto);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA + CHECK SULL'ENTITY DESIDERATA RESTITUITA
//        desiderata = this.controllerDesiderata.addPreference(uuidDto);
//
//        Optional<Preference> _pref = preferenceDao.findById(desiderata.getId());
//        int actualUserPrefId1;
//        if(_pref.isPresent()) {
//            actualUserPrefId1 = Math.toIntExact(_pref.get().getDoctors().get(0).getId());
//        } else throw new RuntimeException();
//
//        Assert.assertEquals(16, desiderata.getDate().getDayOfMonth());
//        Assert.assertEquals(2, desiderata.getDate().getMonthValue());
//        Assert.assertEquals(2024, desiderata.getDate().getYear());
//        Assert.assertEquals(utenteId, actualUserPrefId1);
//
//    }
//
//    @Test
//    @Transactional
//    public void testAggiungiDesiderata2() throws DatabaseException {     //NB: BUG - findByEmailAndPassword() returns multiple results.
//        //VARIABILI UTILI
//        Preference desiderata;
//
//        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
//        User u = utenteDao.findByEmail("domenicoverde@gmail.com");
//        long utenteId = u.getId();
//        PreferenceDTOIn inDto = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceInWithUIDDTO uuidDto = new PreferenceInWithUIDDTO(utenteId, inDto);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA + CHECK SULL'ENTITY DESIDERATA RESTITUITA
//        desiderata = this.controllerDesiderata.addPreference(uuidDto);
//
//        Optional<Preference> _pref = preferenceDao.findById(desiderata.getId());
//        int actualUserPrefId1;
//        if(_pref.isPresent()) {
//            actualUserPrefId1 = Math.toIntExact(_pref.get().getDoctors().get(0).getId());
//        } else throw new RuntimeException();
//
//        Assert.assertEquals(16, desiderata.getDate().getDayOfMonth());
//        Assert.assertEquals(2, desiderata.getDate().getMonthValue());
//        Assert.assertEquals(2024, desiderata.getDate().getYear());
//        Assert.assertEquals(utenteId, actualUserPrefId1);
//    }
//
//    @Test(expected = DatabaseException.class)
//    @Transactional
//    public void testAggiungiDesiderataFail() throws DatabaseException {
//        //CREAZIONE DI UN NUOVO DTO DESIDERATA + DEFINIZIONE DI UN UTENTE ID INESISTENTE
//        long utenteId = -100;
//        PreferenceDTOIn inDto = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceInWithUIDDTO uuidDto = new PreferenceInWithUIDDTO(utenteId, inDto);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO (CHE DOVREBBE LANCIARE L'ECCEZIONE)
//        this.controllerDesiderata.addPreference(uuidDto);
//    }
//
//    @Test
//    @Transactional
//    public void testCancellaDesiderata() throws DatabaseException {
//        //VARIABILI UTILI
//        Preference desiderata;
//
//        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE+
//        User u = utenteDao.findByEmail("domenicoverde@gmail.com");
//        long utenteId = u.getId();
//        PreferenceDTOIn inDto = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceInWithUIDDTO uuidDto = new PreferenceInWithUIDDTO(utenteId, inDto);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA E POI RIMUOVERLO
//        desiderata = this.controllerDesiderata.addPreference(uuidDto);
//        PreferenceDoctorIDDTO docIddDto = new PreferenceDoctorIDDTO(utenteId, desiderata.getId());
//        this.controllerDesiderata.deletePreference(docIddDto);
//
//    }
//
//    @Test(expected = ValidationException.class)
//    @Transactional
//    public void testCancellaDesiderataFail1() throws DatabaseException {
//        //VARIABILI UTILI
//        Preference desiderata;
//
//        //CREAZIONE DI UN NUOVO DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
//        User u = utenteDao.findByEmail("domenicoverde@gmail.com");
//        long utenteId = u.getId();
//        PreferenceDTOIn inDto = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceInWithUIDDTO uuidDto = new PreferenceInWithUIDDTO(utenteId, inDto);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE UN DESIDERATA E POI TENTARE DI RIMUOVERE UN DESIDERATA RELATIVO A UN UTENTE INESISTENTE
//        desiderata = this.controllerDesiderata.addPreference(uuidDto);
//        //Il dto deve generare un'eccezione per id invalid
//        PreferenceDoctorIDDTO docIddDto = new PreferenceDoctorIDDTO(utenteId*(-1), desiderata.getId());
//        this.controllerDesiderata.deletePreference(docIddDto);
//    }
//
//    @Test
//    @Transactional
//    public void testAggiungiDesiderate() throws DatabaseException {
//        //VARIABILI UTILI
//        User u = utenteDao.findByEmail("domenicoverde@gmail.com");
//        long utenteId = u.getId();
//        List<PreferenceDTOOut> preferenceDTOOut;
//        List<PreferenceDTOIn> preferenceDTOIns = new ArrayList<>();
//
//        //CREAZIONE DI ALCUNI DTO DESIDERATA + IDENTIFICAZIONE DELL'UTENTE DA COINVOLGERE
//        PreferenceDTOIn inDto1 = new PreferenceDTOIn(16, 2, 2024, Set.of("MORNING", "AFTERNOON"));
//        PreferenceDTOIn inDto2 = new PreferenceDTOIn(26, 5, 2024, Set.of("MORNING", "AFTERNOON"));
//        preferenceDTOIns.add(inDto1);
//        preferenceDTOIns.add(inDto2);
//        PreferenceListWithUIDDTO dtos = new PreferenceListWithUIDDTO(utenteId, preferenceDTOIns);
//
//        //INVOCAZIONE DEL CONTROLLER APPLICATIVO PER AGGIUNGERE I DESIDERATA + CHECK SUI DESIDERATA RESTITUITI
//        preferenceDTOOut = this.controllerDesiderata.addPreferences(dtos);
//
//        Optional<Preference> _pref1 = preferenceDao.findById(preferenceDTOOut.get(0).getPreferenceId());
//        Optional<Preference> _pref2 = preferenceDao.findById(preferenceDTOOut.get(1).getPreferenceId());
//
//        int actualUserPrefId1, actualUserPrefId2;
//        if(_pref1.isPresent() && _pref2.isPresent()) {
//            actualUserPrefId1 = Math.toIntExact(_pref1.get().getDoctors().get(0).getId());
//            actualUserPrefId2 = Math.toIntExact(_pref2.get().getDoctors().get(0).getId());
//        }
//        else throw new RuntimeException("Preferences saved not found in db");
//
//        Assert.assertEquals(16, preferenceDTOOut.get(0).getDay());
//        Assert.assertEquals(2, preferenceDTOOut.get(0).getMonth());
//        Assert.assertEquals(2024, preferenceDTOOut.get(0).getYear());
//        Assert.assertEquals(utenteId, actualUserPrefId1);
//
//        Assert.assertEquals(26, preferenceDTOOut.get(1).getDay());
//        Assert.assertEquals(5, preferenceDTOOut.get(1).getMonth());
//        Assert.assertEquals(2024, preferenceDTOOut.get(1).getYear());
//        Assert.assertEquals(utenteId, actualUserPrefId2);
//
//    }
//
//}
