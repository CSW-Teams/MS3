package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.dto.RotationDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
@Transactional
public class TestControllerTurni {

    private enum InstanceValidity {
        VALID,
        INVALID
    }

    private static RotationDTO getTurnoDTOInstance(InstanceValidity validity) {
        List<MansioneEnum> mansioni = new ArrayList<>();
        mansioni.add(MansioneEnum.AMBULATORIO);
        ServizioDTO servizioDTO = new ServizioDTO("cardiologia", mansioni);
        switch (validity) {
            case VALID:
                return new RotationDTO(1,
                        TipologiaTurno.NOTTURNO,
                        LocalTime.of(22, 0),
                        Duration.ofHours(8),
                        servizioDTO,
                        MansioneEnum.AMBULATORIO,
                        false,
                        null
                );
            case INVALID:
                return new RotationDTO(1,
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
    public void testCreaTurno(RotationDTO rotationDTO, boolean exceptionExpected) {
        ControllerTurni controllerTurni = new ControllerTurni();
        try {
            controllerTurni.creaTurno(rotationDTO);
            Assertions.assertFalse(exceptionExpected);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.assertTrue(exceptionExpected);
        }
    }

    /*
    * FAIL: NullPointerException su categorieVietate, ma non c'è modo di settare l'attributo categorieVietate nel RotationDTO;
    * FAIL: non viene controllato il booleano giornoSuccessivo
    * */

}