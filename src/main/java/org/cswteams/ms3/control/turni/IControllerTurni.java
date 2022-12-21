package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.TurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;

import java.util.Set;


public interface IControllerTurni {

    Set<TurnoDTO> leggiTurniDiServizio(String servizio);

    Set<TurnoDTO> leggiTurni();

    Turno creaTurno(TurnoDTO turno);



}
