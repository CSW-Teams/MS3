package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.ServizioDTO;
import org.cswteams.ms3.dto.TurnoDTO;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.enums.TipologiaTurno;
import org.cswteams.ms3.exception.TurnoException;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaTurni {

    public static Turno turnoDTOToEntity(TurnoDTO dto) throws TurnoException {
        Turno turno = new Turno(dto.getOraInizio(),dto.getOraFine(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getTipologiaTurno(), dto.isGiornoSuccessivo());
        turno.setCategorieVietate(dto.getCategorieVietate());
        return turno;
    }

    public static TurnoDTO turnoEntityToDTO(Turno entity){
        TurnoDTO dto = TurnoDTO(entity.getId(), entity.getTipologiaTurno(),entity.getOraInizio(), entity.getOraFine(), MappaServizio.servizioEntitytoDTO(entity.getServizio()), entity.isGiornoSuccessivo());
        dto.setCategorieVietate(entity.getCategorieVietate());
        return dto;
    }

    private static TurnoDTO TurnoDTO(Long id, TipologiaTurno tipologiaTurno, LocalTime oraInizio, LocalTime oraFine,
            ServizioDTO servizioEntitytoDTO, boolean giornoSuccessivo) {
        return null;
    }

    public static Set<TurnoDTO> turnoEntityToDTO(List<Turno> turni){
        Set<TurnoDTO> turniDTO = new HashSet<>();
        for(Turno turno: turni){
            turniDTO.add(turnoEntityToDTO(turno));
        }
        return turniDTO;
    }
}
