package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.text.ParseException;
import java.util.Set;

public interface IControllerAssegnazioneTurni {
    Set<AssegnazioneTurnoDTO> leggiTurni() throws ParseException;

    AssegnazioneTurno creaTurno(AssegnazioneTurnoDTO c);

    Set<AssegnazioneTurnoDTO> leggiTurniUtente(Long idUtente) throws ParseException;
}
