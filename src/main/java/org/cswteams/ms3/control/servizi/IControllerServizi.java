package org.cswteams.ms3.control.servizi;

import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.Servizio;

import java.util.Set;

public interface IControllerServizi {

    Set<ServizioDTO> leggiServizi();

    Servizio creaServizio(ServizioDTO servizio);
}
