package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.TurnoDTO;
import org.cswteams.ms3.entity.Turno;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaTurni {

    public static Turno turnoDTOToEntity(TurnoDTO dto) {
        return new Turno(dto.getOraInizio(),dto.getOraFine(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getTipologiaTurno(), dto.getCategorieVietate());
    }

    public static TurnoDTO turnoEntityToDTO(Turno entity){
        return new TurnoDTO(entity.getId(), entity.getTipologiaTurno(),entity.getOraInizio(), entity.getOraFine(), MappaServizio.servizioEntitytoDTO(entity.getServizio()), entity.getCategorieVietate());
    }

    public static Set<TurnoDTO> turnoEntityToDTO(List<Turno> turni){
        Set<TurnoDTO> turniDTO = new HashSet<>();
        for(Turno turno: turni){
            turniDTO.add(turnoEntityToDTO(turno));
        }
        return turniDTO;
    }
}
