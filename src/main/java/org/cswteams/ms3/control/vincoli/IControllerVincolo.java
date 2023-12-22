package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.constraint.ConfigVincoli;
import org.cswteams.ms3.entity.constraint.Constraint;

import java.util.List;

public interface IControllerVincolo {

    List<Constraint> leggiVincoli();

    ConfigVincoli aggiornaVincoli(ConfigVincoli configurazione);

    ConfigVincoli leggiConfigurazioneVincoli();

}
