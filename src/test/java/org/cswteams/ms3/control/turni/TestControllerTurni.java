package org.cswteams.ms3.control.turni;

import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
@Transactional
public class TestControllerTurni {
/*
    private enum InstanceValidity {
        VALID,
        INVALID
    }

    private static TurnoDTO getTurnoDTOInstance(InstanceValidity validity) {
        List<MansioneEnum> mansioni = new ArrayList<>();
        mansioni.add(MansioneEnum.AMBULATORIO);
        ServizioDTO servizioDTO = new ServizioDTO("cardiologia", mansioni);
        switch (validity) {
            case VALID:
                return new TurnoDTO(1,
                        TipologiaTurno.NOTTURNO,
                        LocalTime.of(22, 0),
                        Duration.ofHours(8),
                        servizioDTO,
                        MansioneEnum.AMBULATORIO,
                        false,
                        null
                );
            case INVALID:
                return new TurnoDTO(1,
                        TipologiaTurno.MATTUTINO,
                        LocalTime.of(22, 0),
                        Duration.ofHours(8),
                        null,
                        MansioneEnum.AMBULATORIO,
                        false,
                        null
                );
            default:
                return null;
        }
    }

    private static Stream<Arguments> creaTurnoParams() {
        return Stream.of(
                Arguments.of(getTurnoDTOInstance(InstanceValidity.VALID), false),
                Arguments.of(getTurnoDTOInstance(InstanceValidity.INVALID), true),
                Arguments.of(null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("creaTurnoParams")
    public void testCreaTurno(TurnoDTO turnoDTO, boolean exceptionExpected) {
        ControllerTurni controllerTurni = new ControllerTurni();
        try {
            controllerTurni.creaTurno(turnoDTO);
            Assertions.assertFalse(exceptionExpected);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.assertTrue(exceptionExpected);
        }
    }*/

    /*
    * FAIL: NullPointerException su categorieVietate, ma non c'è modo di settare l'attributo categorieVietate nel TurnoDTO;
    * FAIL: non viene controllato il booleano giornoSuccessivo
    * */

}