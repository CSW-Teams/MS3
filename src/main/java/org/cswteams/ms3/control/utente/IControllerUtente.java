package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;
import java.util.List;
import java.util.Set;


public interface IControllerUtente {

    Set<UtenteDTO> leggiUtenti();

    Object creaUtente(UtenteDTO c);

}
