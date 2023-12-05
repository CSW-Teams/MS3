package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.TurnoDTO;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.exception.TurnoException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaTurni {

    public static Turno turnoDTOToEntity(TurnoDTO dto) throws TurnoException {
        Turno turno = new Turno(dto.getOraInizio(),dto.getDurata(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getMansione(), dto.getTipologiaTurno(), dto.getRuoliNumero(),dto.isReperibilitaAttiva());
        turno.setCategorieVietate(dto.getCategorieVietate());
        return turno;
    }

    public static TurnoDTO turnoEntityToDTO(Turno entity){
        TurnoDTO dto = new TurnoDTO(entity.getId(), entity.getTipologiaTurno(),entity.getOraInizio(), entity.getDurata(), MappaServizio.servizioEntitytoDTO(entity.getServizio()), entity.getMansione(), entity.isReperibilitaAttiva(), entity.getRuoliNumero());
        dto.setCategorieVietate(entity.getCategorieVietate());
        return dto;
    }


    public static Set<TurnoDTO> turnoEntityToDTO(List<Turno> turni){
        Set<TurnoDTO> turniDTO = new HashSet<>();
        for(Turno turno: turni){
            turniDTO.add(turnoEntityToDTO(turno));
        }
        return turniDTO;
    }
}
