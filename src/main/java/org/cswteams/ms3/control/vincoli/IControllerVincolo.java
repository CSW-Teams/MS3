package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.entity.vincoli.ConfigVincoli;
import org.cswteams.ms3.entity.vincoli.Vincolo;
import java.util.List;

public interface IControllerVincolo {

    List<Vincolo> leggiVincoli();

    ConfigVincoli aggiornaVincoli(ConfigVincoli configurazione);

    ConfigVincoli leggiConfigurazioneVincoli();

}
