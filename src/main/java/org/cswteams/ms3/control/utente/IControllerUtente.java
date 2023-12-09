package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.dto.UtenteDTO;

import java.util.Set;


public interface IControllerUtente {

    Set<UtenteDTO> leggiUtenti();

    Object creaUtente(UtenteDTO c);

    UtenteDTO leggiUtente(long idUtente);

}
