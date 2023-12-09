package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.dao.ConditionDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.CategoriaUtenteDTO;
import org.cswteams.ms3.entity.doctor.Doctor;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Unit test for the class {@link ControllerCategorieUtente}.
 */

@SpringBootTest
@Transactional
public class TestControllerCategorieDoctor {

    private enum InstanceValidity {
        VALID,
        INVALID
    }

    private enum InstanceType {
        STATO,
        SPECIALIZZAZIONE,
        TURNAZIONE
    }

    private static long idUtente;
    private Doctor testDoctor;

    @Autowired
    private ControllerCategorieUtente controllerCategorieUtente = new ControllerCategorieUtente();
    @Autowired
    private ConditionDao conditionDao;

    @Autowired
    private UtenteDao utenteDao;

    private static Stream<Arguments> leggiCategorieUtenteParams() {
        return Stream.of(
                //           id, exception expected
                Arguments.of(1, false),
                Arguments.of(-1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("leggiCategorieUtenteParams")
    public void testLeggiCategorieUtente(long id, boolean exceptionExpected) {
        try {
            Set<CategoriaUtenteDTO> results = controllerCategorieUtente.leggiCategorieUtente(id);
            assertTrue(results.size() >= 1);
        } catch (Exception e) {
            assertTrue(exceptionExpected);      // FAIL: nel caso di id non esistente, non viene sollevata nessuna eccezione
        }
    }

    private static Stream<Arguments> leggiSpecializzazioniUtenteParams() {
        return Stream.of(
                //           id, exception expected
                Arguments.of(1, false),
                Arguments.of(-1, true)
        );
    }

    @ParameterizedTest
    @MethodSource("leggiSpecializzazioniUtenteParams")
    public void testLeggiSpecializzazioniUtente(long id, boolean exceptionExpected) {
        try {
            Set<CategoriaUtenteDTO> results = controllerCategorieUtente.leggiSpecializzazioniUtente(id);
            assertTrue(results.size() >= 1);
        } catch (Exception e) {
            assertTrue(exceptionExpected);
        }
    }

    private CategoriaUtenteDTO getCategoriaUtenteDTOInstance(Categoria categoria, InstanceValidity validity) {
        CategoriaUtenteDTO categoriaUtenteDTO = null;
        if (validity == null)
            return null;
        switch (validity) {
            case VALID:
                categoriaUtenteDTO = new CategoriaUtenteDTO(categoria, "2023-11-19T00:00:00", "2023-11-29T00:00:00");
                return categoriaUtenteDTO;
            case INVALID:
                categoriaUtenteDTO = new CategoriaUtenteDTO(null, "2023-11-19T00:00:00", "2023-11-29T00:00:00");
                return categoriaUtenteDTO;
        }


        return null;
    }

    private static Stream<Arguments> aggiungiTurnazioneUtenteParams() {
        return Stream.of(
                // utenteID, c, exceptionExpected
                Arguments.of(InstanceValidity.VALID, InstanceValidity.VALID, false),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.VALID, true),
                Arguments.of(InstanceValidity.VALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.VALID, null, true),
                Arguments.of(InstanceValidity.INVALID, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("aggiungiTurnazioneUtenteParams")
    public void testAggiungiTurnazioneUtente(InstanceValidity validityIdUtente, InstanceValidity validityCategoriaUtente, boolean exceptionExpected) {

        Categoria categoriaTurnazione = new Categoria("turnazione", TipoCategoriaEnum.TURNAZIONE);
        conditionDao.saveAndFlush(categoriaTurnazione);

        if (validityIdUtente == InstanceValidity.VALID) {
            testDoctor = new Doctor("Mario", "Rossi", "RSSMRA******", LocalDate.of(2000, 1, 1), "mario@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
            utenteDao.saveAndFlush(testDoctor);
            idUtente = testDoctor.getId();
        } else {
            idUtente = -1;
        }

        CategoriaUtenteDTO c = getCategoriaUtenteDTOInstance(categoriaTurnazione, validityCategoriaUtente);
        try {
            controllerCategorieUtente.aggiungiTurnazioneUtente(c, idUtente);
            assertEquals(List.copyOf(controllerCategorieUtente.leggiTurnazioniUtente(idUtente)).get(0).getCategoria(), categoriaTurnazione);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(exceptionExpected);
        }
    }


    // leggiTurnazioniUtente è testato nel metodo precedente, con id valido
    @Test
    public void testLeggiTurnazioniUtenteNonValido() {
        try {
            controllerCategorieUtente.leggiTurnazioniUtente((long)-1);
            assert false;
        } catch (Exception e) {
            assert true;
        }
    }


    private static Stream<Arguments> aggiungiStatoUtenteParams() {
        return Stream.of(
                // utenteID, c, exceptionExpected
                Arguments.of(InstanceValidity.VALID, InstanceValidity.VALID, false),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.VALID, true),
                Arguments.of(InstanceValidity.VALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.VALID, null, true),
                Arguments.of(InstanceValidity.INVALID, null, true)
        );
    }
    @ParameterizedTest
    @MethodSource("aggiungiStatoUtenteParams")
    public void testAggiungiStatoUtente(InstanceValidity validityIdUtente, InstanceValidity validityCategoriaUtente, boolean exceptionExpected) {
        Categoria categoriaStato = new Categoria("stato", TipoCategoriaEnum.STATO);
        conditionDao.saveAndFlush(categoriaStato);

        if (validityIdUtente == InstanceValidity.VALID) {
            testDoctor = new Doctor("Mario", "Rossi", "RSSMRA******", LocalDate.of(2000, 1, 1), "mario@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
            utenteDao.saveAndFlush(testDoctor);
            idUtente = testDoctor.getId();
        } else {
            idUtente = -1;
        }

        CategoriaUtenteDTO c = getCategoriaUtenteDTOInstance(categoriaStato, validityCategoriaUtente);
        try {
            controllerCategorieUtente.aggiungiStatoUtente(c, idUtente);
            assertEquals(testDoctor.getStato().get(0).getCategoria(), categoriaStato);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(exceptionExpected);
        }
    }


    private static Stream<Arguments> cancellaStatoParams() {
        return Stream.of(
                // idUtente, idCategoria, exceptionExpected
                Arguments.of(InstanceValidity.VALID, InstanceValidity.VALID, false),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.VALID, true),
                Arguments.of(InstanceValidity.VALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.INVALID, true)
        );
    }

    @ParameterizedTest
    @MethodSource("cancellaStatoParams")
    public void testCancellaStato(InstanceValidity validityIdUtente, InstanceValidity validityIdCategoria, boolean exceptionExpected) {
        Categoria categoriaStato = new Categoria("stato", TipoCategoriaEnum.STATO);
        conditionDao.saveAndFlush(categoriaStato);

        if (validityIdUtente == InstanceValidity.VALID) {
            testDoctor = new Doctor("Mario", "Rossi", "RSSMRA******", LocalDate.of(2000, 1, 1), "mario@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
            utenteDao.saveAndFlush(testDoctor);
            idUtente = testDoctor.getId();
        } else {
            idUtente = -1;
        }

        CategoriaUtenteDTO c = getCategoriaUtenteDTOInstance(categoriaStato, InstanceValidity.VALID);

        if (validityIdUtente == InstanceValidity.VALID) {
            try {
                controllerCategorieUtente.aggiungiStatoUtente(c, idUtente);
            } catch (Exception e) {
                assert false;
            }
        }

        if (validityIdCategoria == InstanceValidity.VALID) {
            try {
                controllerCategorieUtente.cancellaStato(testDoctor.getStato().get(0).getId(), idUtente);
                assertTrue(testDoctor.getStato().isEmpty());
            } catch (Exception e) {
                assertTrue(exceptionExpected);
            }
        } else {
            try {
                controllerCategorieUtente.cancellaStato(testDoctor.getStato().get(0).getId(), idUtente);      // FAIL: non viene controllato che lo stato sia presente
                assert false;
            } catch (Exception e) {
                assertTrue(exceptionExpected);
            }
        }
    }


    private static Stream<Arguments> cancellaRotazioneParams() {
        return Stream.of(
                // idUtente, idCategoria, exceptionExpected
                Arguments.of(InstanceValidity.VALID, InstanceValidity.VALID, false),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.VALID, true),
                Arguments.of(InstanceValidity.VALID, InstanceValidity.INVALID, true),
                Arguments.of(InstanceValidity.INVALID, InstanceValidity.INVALID, true)
        );
    }

    @ParameterizedTest
    @MethodSource("cancellaStatoParams")
    public void testCancellaRotazione(InstanceValidity validityIdUtente, InstanceValidity validityIdCategoria, boolean exceptionExpected) {
        Categoria categoriaTurnazione = new Categoria("turnazione", TipoCategoriaEnum.TURNAZIONE);
        conditionDao.saveAndFlush(categoriaTurnazione);

        if (validityIdUtente == InstanceValidity.VALID) {
            testDoctor = new Doctor("Mario", "Rossi", "RSSMRA******", LocalDate.of(2000, 1, 1), "mario@gmail.com", "", RuoloEnum.SPECIALIZZANDO, AttoreEnum.UTENTE);
            utenteDao.saveAndFlush(testDoctor);
            idUtente = testDoctor.getId();
        } else {
            idUtente = -1;
        }

        CategoriaUtenteDTO c = getCategoriaUtenteDTOInstance(categoriaTurnazione, InstanceValidity.VALID);

        if (validityIdUtente == InstanceValidity.VALID) {
            try {
                controllerCategorieUtente.aggiungiTurnazioneUtente(c, idUtente);
            } catch (Exception e) {
                assert false;
            }
        }

        if (validityIdCategoria == InstanceValidity.VALID) {
            try {
                controllerCategorieUtente.cancellaRotazione(testDoctor.getTurnazioni().get(0).getId(), idUtente);
                assertTrue(testDoctor.getTurnazioni().isEmpty());
            } catch (Exception e) {
                assertTrue(exceptionExpected);
            }
        } else {
            try {
                controllerCategorieUtente.cancellaRotazione(testDoctor.getTurnazioni().get(0).getId(), idUtente);      // FAIL: non viene controllato che la turnazione sia presente
                assert false;
            } catch (Exception e) {
                assertTrue(exceptionExpected);
            }
        }
    }

}
