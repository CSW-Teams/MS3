package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.Servizio;
import org.cswteams.ms3.entity.Utente;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaServizio {

    public static Servizio servizioDTOtoEntity(ServizioDTO dto){
        return new Servizio(dto.getNome(), dto.getMansioni());
    }

    public static Set<ServizioDTO> servizioEntitytoDTO(List<Servizio> servizi){
        Set<ServizioDTO> serviziDTO = new HashSet<>();
        for (Servizio servizio: servizi){
            serviziDTO.add(servizioEntitytoDTO(servizio));
        }
        return serviziDTO;
    }

    public static ServizioDTO servizioEntitytoDTO(Servizio entity){
        return new ServizioDTO(entity.getNome(), entity.getMansioni());
    }

}
