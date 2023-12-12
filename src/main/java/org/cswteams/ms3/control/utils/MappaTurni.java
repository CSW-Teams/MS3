package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.RotationDTO;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.exception.TurnoException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaTurni {

    public static Shift turnoDTOToEntity(RotationDTO dto) throws TurnoException {
        Shift shift = new Shift(dto.getOraInizio(),dto.getDurata(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getMansione(), dto.getTipologiaTurno(), dto.getRuoliNumero(),dto.isReperibilitaAttiva());
        shift.setBannedConditions(dto.getBannedConditions());
        return shift;
    }

    public static RotationDTO turnoEntityToDTO(Shift entity){
        RotationDTO dto = new RotationDTO(entity.getId(), entity.getTipologiaTurno(),entity.getOraInizio(), entity.getDurata(), MappaServizio.servizioEntitytoDTO(entity.getServizio()), entity.getMansione(), entity.isReperibilitaAttiva(), entity.getRuoliNumero());
        dto.setBannedConditions(entity.getBannedConditions());
        return dto;
    }


    public static Set<RotationDTO> turnoEntityToDTO(List<Shift> turni){
        Set<RotationDTO> turniDTO = new HashSet<>();
        for(Shift shift : turni){
            turniDTO.add(turnoEntityToDTO(shift));
        }
        return turniDTO;
    }
}
