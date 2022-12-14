package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


public interface IControllerTurni {

    Set<AssegnazioneTurnoDTO> leggiTurni();

    Object creaTurno(AssegnazioneTurnoDTO c);

    Set<AssegnazioneTurnoDTO> leggiTurniUtente(Long idUtente);

}
