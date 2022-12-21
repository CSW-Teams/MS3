package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.text.ParseException;
import java.util.Set;

public interface IControllerAssegnazioneTurni {
    Set<AssegnazioneTurnoDTO> leggiTurniAssegnati() throws ParseException;

    AssegnazioneTurno creaTurnoAssegnato(AssegnazioneTurnoDTO c);

    Set<AssegnazioneTurnoDTO> leggiTurniUtente(Long idUtente) throws ParseException;
}
