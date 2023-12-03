package org.cswteams.ms3.control.registrazione;

import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.RegistrazioneDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.*;
import javax.validation.constraints.NotNull;

@Service
public class ControllerRegistrazione implements IControllerRegistrazione {

    @Autowired
    private UtenteDao utenteDao;



    private static boolean validaCodiceFiscale(String codiceFiscale) {

        String regex = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(codiceFiscale);
        return matcher.matches();

    }



    @Override
    public UtenteDTO registraUtente(@NotNull RegistrazioneDTO registrazioneDTO) {

        //sanity check sull'input: il nuovo utente deve avere un nome, un cognome, un codice fiscale e una password correttamente inizializzati
        if(registrazioneDTO.getNome() == "" || registrazioneDTO.getCognome() == "" || !validaCodiceFiscale(registrazioneDTO.getCodiceFiscale()) || registrazioneDTO.getPassword() == "") {
            return null;
        }

        Utente u = new Utente(registrazioneDTO.getNome(),
                registrazioneDTO.getCognome(),
                registrazioneDTO.getCodiceFiscale(),
                registrazioneDTO.getDataNascita(),
                registrazioneDTO.getEmail(),
                registrazioneDTO.getPassword(),
                registrazioneDTO.getRuolo(),
                registrazioneDTO.getAttore()
                );

        utenteDao.saveAndFlush(u);

        return MappaUtenti.utenteEntitytoDTO(u);

    }

}
