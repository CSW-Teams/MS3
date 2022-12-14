package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Utente;

import java.util.HashSet;
import java.util.Set;

public class MappaUtenti {

    public static Utente utenteDTOtoENTITY(UtenteDTO utenteDTO) {
        return null;
    }

    public static UtenteDTO utenteENTITYtoDTO(Utente utente) {
        UtenteDTO dto = new UtenteDTO(utente.getId(),utente.getNome(),utente.getCognome(),utente.getDataNascita(), utente.getCodiceFiscale(), utente.getRuoloEnum(), utente.getEmail());
        return dto;
    }

    public static Set<UtenteDTO> utenteENTITYtoDTO(Set<Utente> utenti){
        Set<UtenteDTO> utenteDTOS = new HashSet<>();
        for (Utente entity: utenti){
            utenteDTOS.add(utenteENTITYtoDTO(entity));
        }
        return utenteDTOS;
    }

}
