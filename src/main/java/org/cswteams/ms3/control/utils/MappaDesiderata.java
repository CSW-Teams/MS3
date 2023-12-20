package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.entity.Doctor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MappaDesiderata {

    public static Preference desiderataDtoToEntity(DesiderataDTO dto, Doctor doctor){
        return new Preference(LocalDate.of(dto.getAnno(), dto.getMese(), dto.getGiorno()), dto.getTipologieTurni(), doctor);

    }

    public static List<Preference> desiderataDtoToEntity(List<DesiderataDTO> dtos, Doctor doctor){
        List<Preference> desiderata = new ArrayList<>();
        for(DesiderataDTO dto: dtos){
            desiderata.add(desiderataDtoToEntity(dto, doctor));
        }
        return desiderata;
    }

    public static DesiderataDTO desiderataToDto(Preference entity){
        return new DesiderataDTO(entity.getId(), entity.getData(), entity.getTipologieTurnoCoinvolte());
    }

    public static List<DesiderataDTO> desiderataToDto(List<Preference> entities){
        List<DesiderataDTO> desiderataDTO = new ArrayList<>();

        for(Preference desiderata: entities){
            desiderataDTO.add(desiderataToDto(desiderata));
        }
        return desiderataDTO;
    }
}
