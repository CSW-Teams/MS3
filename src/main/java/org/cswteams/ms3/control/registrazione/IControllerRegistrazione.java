package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.dto.RegistrazioneDTO;
import org.cswteams.ms3.dto.UtenteDTO;

public interface IControllerRegistrazione {

    UtenteDTO registraUtente(RegistrazioneDTO registrazioneDTO);
}
