package org.cswteams.ms3.control.giustificaForzatura;

import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.enums.AttoreEnum;
import org.cswteams.ms3.enums.RuoloEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/***********************************************************************************
 * This class has the responsibility of testing the saveGiustificazione method of  *
 * class ControllerGiustificaForzatura. In particular the code is divided in 3     *
 * different parts:                                                                *
 * -  SETUP
 * -  DOMAIN PARTITION
 * -  TEST
 ***********************************************************************************/
@SpringBootTest
class ControllerGiustificaForzaturaSaveGiustificazioneTest extends ControllerGiustificaForzaturaTest{
    private static final Logger log = LoggerFactory.getLogger(ControllerGiustificaForzaturaSaveGiustificazioneTest.class);
    private static Schedule scheduleMock;
    @Autowired
    ControllerGiustificaForzatura controllerGiustificaForzatura;

    /**************************************************************
     *                          SETUP                             *
     *************************************************************/

    // SetUp and CleanUp of the testing environment
    @BeforeAll
    static void setUp() {
        scheduleMock = mock(Schedule.class);

        log.info("[DEBUG] [TEST] Set-up going on....");
        when(scheduleMock.isIllegal()).thenReturn(true);
    }

    @AfterAll
    static void tearDown() {
        /* pass */
    }


    /*************************************************************
     *                    DOMANI PARTITION                       *
     *************************************************************/


    private static GiustificazioneForzaturaVincoliDTO constructorGiustificazioneForzaturaVincoliDTOPartition(
            Set<UtenteDTO> setUtenti,
            int year,
            int day,
            int month,
            ServizioDTO service,
            String justificationID,
            TipologiaTurno turnType
    ){
        GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoliDTO = new GiustificazioneForzaturaVincoliDTO();


        //Populate justification
        giustificazioneForzaturaVincoliDTO.setUtentiAllocati(setUtenti);
        giustificazioneForzaturaVincoliDTO.setAnno(year);
        giustificazioneForzaturaVincoliDTO.setGiorno(day);
        giustificazioneForzaturaVincoliDTO.setMese(month);
        giustificazioneForzaturaVincoliDTO.setServizio(service);
        giustificazioneForzaturaVincoliDTO.setUtenteGiustificatoreId(justificationID);
        giustificazioneForzaturaVincoliDTO.setTipologiaTurno(turnType);

        return giustificazioneForzaturaVincoliDTO;
    }

    /**************************************************************************************
     *                                                                                    *
     * DOMAIN PARTITIONING FOR giustificazioneForzaturaVincoliDTO ATTRIBUTE IN PARTITION1 *
     *                                                                                    *
     *************************************************************************************/
    private static GiustificazioneForzaturaVincoliDTO generateGiustifica(int caseNumber) {
        GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoliDTO;

        // Initialize the system having only one user
        Set<UtenteDTO> setUtenti = new HashSet<>();
        List<CategoriaUtente> categorie = new ArrayList<>();
        List<CategoriaUtente> specializzazioni = new ArrayList<>();
        categorie.add(new CategoriaUtente());
        specializzazioni.add(new CategoriaUtente());

        UtenteDTO user = new UtenteDTO(
                (long) 1,
                "Simone",
                "Staccone",
                LocalDate.of(2020, 1, 8),
                "STCSMN0016D0O",
                RuoloEnum.SPECIALIZZANDO,
                "simone.staccone@virgilio.it",
                "psw",
                categorie,
                specializzazioni,
                AttoreEnum.UTENTE
        );

        UtenteDTO emptyUser = new UtenteDTO();



        switch (caseNumber) {
            case 1:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 2:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2023,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 3:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,10,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 4:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,3,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 5:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("radiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 6:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"2",TipologiaTurno.MATTUTINO);
                break;
            case 7:
                setUtenti.add(user);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.NOTTURNO);
                break;
            case 8:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 9:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2023,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 10:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,10,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 11:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,3,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 12:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("radiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 13:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"2",TipologiaTurno.MATTUTINO);
                break;
            case 14:
                setUtenti.add(emptyUser);
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.NOTTURNO);
                break;
            case 15:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 16:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2023,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 17:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,10,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 18:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,3,new ServizioDTO("cardiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 19:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("radiologia"),"1",TipologiaTurno.MATTUTINO);
                break;
            case 20:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"2",TipologiaTurno.MATTUTINO);
                break;
            case 21:
                setUtenti = null;
                giustificazioneForzaturaVincoliDTO = constructorGiustificazioneForzaturaVincoliDTOPartition(setUtenti,2020,8,1,new ServizioDTO("cardiologia"),"1",TipologiaTurno.NOTTURNO);
                break;
            default:
                throw new IllegalAccessError();
        }
        return giustificazioneForzaturaVincoliDTO;
    }





    /* *********************************************************************
    * DOMAIN PARTITIONING FOR PARTITION1
    * () -> (setUsers, year, day, month, service, justificationID, turnType)
    * (valid instance)
    *       -> users[1](1,Simone,Staccone,"2020-08-01","STCSMN0016D0O",SPECIALIZZANDO,"simone.staccone@virgilio.it","psw",categories, specializations,UTENTE),
    *           2020,08,01,"cardiologia","1",MATTUTINO)
    * (invalid instance)
    *       -> (
    *           users[1](1,Simone,Staccone,"2020-08-01","STCSMN0016D0O",SPECIALIZZANDO,"simone.staccone@virgilio.it","psw",categories, specializations,UTENTE)
    *           ,2023,08,01,"cardiologia","1",MATTUTINO
    *           )
    *       -> (
    *           users[1](1,Simone,Staccone,"2020-08-01","STCSMN0016D0O",SPECIALIZZANDO,"simone.staccone@virgilio.it","psw",categories, specializations,UTENTE)
    *           ,2020,09,01,"cardiologia","1",MATTUTINO
    *           )
    *       -> (
    *               ...
    *           )
    * (null instance)
    * The main difference in invalid instance is the difference between UtenteDTO and test case
    * configuration: this is a single class domain partitioning for all attributes except for
    * setUsers (good/wrong/null)
    * **********************************************************************/
    private static Stream<Arguments> partition() {
        return Stream.of(
                //setUsers full
                Arguments.of(generateGiustifica(1),false),
                Arguments.of(generateGiustifica(2),true),
                Arguments.of(generateGiustifica(3),true),
                Arguments.of(generateGiustifica(4),true),
                Arguments.of(generateGiustifica(5),true),
                Arguments.of(generateGiustifica(6),true),
                Arguments.of(generateGiustifica(7),true),


                //setUsers empty
                Arguments.of(generateGiustifica(8),true),
                Arguments.of(generateGiustifica(9),true),
                Arguments.of(generateGiustifica(10),true),
                Arguments.of(generateGiustifica(11),true),
                Arguments.of(generateGiustifica(12),true),
                Arguments.of(generateGiustifica(13),true),
                Arguments.of(generateGiustifica(14),true),

                //setUsers null
                Arguments.of(generateGiustifica(15),true),
                Arguments.of(generateGiustifica(16),true),
                Arguments.of(generateGiustifica(17),true),
                Arguments.of(generateGiustifica(18),true),
                Arguments.of(generateGiustifica(19),true),
                Arguments.of(generateGiustifica(20),true),
                Arguments.of(generateGiustifica(21),true),

                //null object
                Arguments.of(null,true)
        );
    }



    /**************************************************************
     *                           TEST                             *
     *************************************************************/


    // Actual test
    @ParameterizedTest
    @MethodSource("partition")
    @Override
    public void saveGiustificazione(GiustificazioneForzaturaVincoliDTO giustificazioneForzaturaVincoli, boolean expectedResult) {
        assertNotNull(controllerGiustificaForzatura); //Check autowiring worked
        if (!expectedResult) {
            assertDoesNotThrow(() -> controllerGiustificaForzatura.saveGiustificazione(giustificazioneForzaturaVincoli));
        } else {
            assertThrows(Exception.class, () -> controllerGiustificaForzatura.saveGiustificazione(giustificazioneForzaturaVincoli));
        }

    }

}