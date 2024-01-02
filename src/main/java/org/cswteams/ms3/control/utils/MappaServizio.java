package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaServizio {

    /*
    public static MedicalService servizioDTOtoEntity(MedicalServiceDTO dto){
        return new MedicalService(dto.getNome(), dto.getMansioni());
    }
*/

    public static Set<MedicalServiceDTO> servizioEntitytoDTO(List<MedicalService> servizi){
        Set<MedicalServiceDTO> serviziDTO = new HashSet<>();
        for (MedicalService servizio: servizi){
            serviziDTO.add(servizioEntitytoDTO(servizio));
        }
        return serviziDTO;
    }

    public static MedicalServiceDTO servizioEntitytoDTO(MedicalService entity){
        return new MedicalServiceDTO(entity.getLabel(), entity.getTasks());
    }

}
