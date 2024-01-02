package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.Utente;

import java.util.ArrayList;
import java.util.List;

public class MappaDesiderata {

    public static Desiderata desiderataDtoToEntity(DesiderataDTO dto, Utente utente){
        return new Desiderata(dto.getGiorno(), dto.getTipologieTurni(), utente);
    }

    public static List<Desiderata> desiderataDtoToEntity(List<DesiderataDTO> dtos, Utente utente){
        List<Desiderata> desiderata = new ArrayList<>();
        for(DesiderataDTO dto: dtos){
            desiderata.add(desiderataDtoToEntity(dto,utente));
        }
        return desiderata;
    }

    public static DesiderataDTO desiderataToDto(Desiderata entity){
        return new DesiderataDTO(entity.getId(), entity.getData(), entity.getTipologieTurnoCoinvolte());
    }

    public static List<DesiderataDTO> desiderataToDto(List<Desiderata> entities){
        List<DesiderataDTO> desiderataDTO = new ArrayList<>();

        for(Desiderata desiderata: entities){
            desiderataDTO.add(desiderataToDto(desiderata));
        }
        return desiderataDTO;
    }
}
