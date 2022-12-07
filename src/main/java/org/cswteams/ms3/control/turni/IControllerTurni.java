package org.cswteams.ms3.control.turni;

import org.cswteams.ms3.entity.Turno;
import java.sql.Timestamp;
import java.util.List;


public interface IControllerTurni {

    List<Turno> leggiTurni();

    Object creaTurno(Turno c);

    List <Turno> leggiTurniUtente(Long idUtente);

    List <Turno> leggiTurnoDaSlot(Timestamp inizio, Timestamp fine);
}
