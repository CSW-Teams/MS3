package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.dto.RotationDTO;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.exception.TurnoException;

import java.util.Set;


public interface IControllerTurni {

    Set<RotationDTO> leggiTurniDiServizio(String servizio);

    Set<RotationDTO> leggiTurni();

    Shift creaTurno(RotationDTO turno) throws TurnoException;


}
