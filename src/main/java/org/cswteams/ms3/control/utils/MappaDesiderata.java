package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.DesiderataDTO;
import org.cswteams.ms3.entity.Desiderata;
import org.cswteams.ms3.entity.doctor.Doctor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MappaDesiderata {

    public static Desiderata desiderataDtoToEntity(DesiderataDTO dto, Doctor doctor){
        return new Desiderata(LocalDate.of(dto.getAnno(), dto.getMese(), dto.getGiorno()), dto.getTipologieTurni(), doctor);
    }

    public static List<Desiderata> desiderataDtoToEntity(List<DesiderataDTO> dtos, Doctor doctor){
        List<Desiderata> desiderata = new ArrayList<>();
        for(DesiderataDTO dto: dtos){
            desiderata.add(desiderataDtoToEntity(dto, doctor));
        }
        return desiderata;
    }

    public static DesiderataDTO desiderataToDto(Desiderata entity){
        return new DesiderataDTO(entity.getId(), entity.getData().getDayOfMonth(), entity.getData().getMonthValue(),entity.getData().getYear(), entity.getTipologieTurnoCoinvolte());
    }

    public static List<DesiderataDTO> desiderataToDto(List<Desiderata> entities){
        List<DesiderataDTO> desiderataDTO = new ArrayList<>();

        for(Desiderata desiderata: entities){
            desiderataDTO.add(desiderataToDto(desiderata));
        }
        return desiderataDTO;
    }
}
