package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;

import java.text.ParseException;
import java.util.Set;

public interface IControllerAssegnazioneTurni {
    Set<AssegnazioneTurnoDTO> leggiTurniAssegnati() throws ParseException;

    ConcreteShift creaTurnoAssegnato(RegistraAssegnazioneTurnoDTO c) throws AssegnazioneTurnoException;

    Set<AssegnazioneTurnoDTO> leggiTurniUtente(Long idUtente) throws ParseException;

    ConcreteShift leggiTurnoByID(long idAssegnazione);

}
