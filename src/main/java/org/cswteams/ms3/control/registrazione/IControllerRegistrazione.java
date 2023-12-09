package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.dto.RegistrazioneDTO;
import org.cswteams.ms3.dto.DoctorDTO;

public interface IControllerRegistrazione {

    DoctorDTO registraUtente(RegistrazioneDTO registrazioneDTO);
}
