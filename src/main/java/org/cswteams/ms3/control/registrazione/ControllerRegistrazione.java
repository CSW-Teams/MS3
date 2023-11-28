package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.RegistrazioneDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class ControllerRegistrazione implements IControllerRegistrazione {

    @Autowired
    private UtenteDao utenteDao;

    @Override
    public UtenteDTO registraUtente(@NotNull RegistrazioneDTO registrazioneDTO) {
        Utente u = new Utente(registrazioneDTO.getNome(),
                registrazioneDTO.getCognome(),
                registrazioneDTO.getCodiceFiscale(),
                registrazioneDTO.getDataNascita(),
                registrazioneDTO.getEmail(),
                registrazioneDTO.getPassword(),
                registrazioneDTO.getRuoloEnum(),
                registrazioneDTO.getAttoreEnum()
                );

        utenteDao.saveAndFlush(u);


        return MappaUtenti.utenteEntitytoDTO(u);
    }

}
