package org.cswteams.ms3.control.utente;

import org.cswteams.ms3.entity.Utente;
import java.util.List;


public interface IControllerUtente {

    List<Utente> leggiUtenti();

    Object creaUtente(Utente c);

}
