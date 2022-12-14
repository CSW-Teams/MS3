package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.entity.AssegnazioneTurno;
import java.sql.Timestamp;
import java.util.List;


public interface IControllerTurni {

    List<AssegnazioneTurno> leggiTurni();

    Object creaTurno(AssegnazioneTurno c);

    List <AssegnazioneTurno> leggiTurniUtente(Long idUtente);

    List <AssegnazioneTurno> leggiTurnoDaSlot(Timestamp inizio, Timestamp fine);
}
