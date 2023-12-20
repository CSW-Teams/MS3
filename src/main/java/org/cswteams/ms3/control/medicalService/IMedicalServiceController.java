package org.cswteams.ms3.control.servizi;

import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.entity.Servizio;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface IControllerServizi {

    Set<ServizioDTO> leggiServizi();
    ServizioDTO leggiServizioByNome(@NotNull String nome);
    Servizio creaServizio(@NotNull ServizioDTO servizio);
}
