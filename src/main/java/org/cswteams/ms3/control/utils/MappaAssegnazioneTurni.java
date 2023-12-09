package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class MappaAssegnazioneTurni {


  /*  public static AssegnazioneTurno assegnazioneTurnoDTOToEntity(AssegnazioneTurnoDTO dto) throws TurnoException {
        // FIXME: DA SISTEMARE, DEVE PRENDERLO DAL DB
        Shift turno = new Shift(dto.getIdTurno(), dto.getInizio().toLocalDateTime().toLocalTime(), dto.getFine().toLocalDateTime().toLocalTime(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getTipologiaTurno(),dto.isGiornoSuccessivoTurno());
        Set<Utente> diGuardia = MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia());
        Set<Utente> reperibili = MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili());
        turno.setMansione(dto.getMansione());

        return new AssegnazioneTurno(dto.getInizio().toLocalDateTime().toLocalDate(), turno, reperibili, diGuardia);
    }*/

    public static AssegnazioneTurnoDTO assegnazioneTurnoToDTO(AssegnazioneTurno entity) {
        ZoneId zone = ZoneId.systemDefault();

        LocalDateTime localDateTimeInizio = LocalDateTime.of(entity.getData(), entity.getShift().getOraInizio());
        long inizioEpoch = localDateTimeInizio.atZone(zone).toEpochSecond();

        LocalDateTime localDateTimeFine = localDateTimeInizio.plus(entity.getShift().getDurata());
        long fineEpoch = localDateTimeFine.atZone(zone).toEpochSecond();

        Set<DoctorDTO> diGuardiaDto = MappaUtenti.utentiEntityToDTO(entity.getUtentiDiGuardia());
        Set<DoctorDTO> reperibiliDto = MappaUtenti.utentiEntityToDTO(entity.getUtentiReperibili());
        Set<DoctorDTO> rimossiDto = MappaUtenti.utentiEntityToDTO(entity.getRetiredDoctors());

        AssegnazioneTurnoDTO dto = new AssegnazioneTurnoDTO(entity.getId(), entity.getShift().getId(), inizioEpoch, fineEpoch, diGuardiaDto, reperibiliDto, MappaServizio.servizioEntitytoDTO(entity.getShift().getServizio()), entity.getShift().getTipologiaTurno(), entity.getShift().isReperibilitaAttiva());
        dto.setMansione(entity.getShift().getMansione());
        dto.setRetiredUsers(rimossiDto);
        return dto;
    }

    public static Set<AssegnazioneTurnoDTO> assegnazioneTurnoToDTO(Set<AssegnazioneTurno> turni) {
        Set<AssegnazioneTurnoDTO> assegnazioneTurnoDTOS = new HashSet<>();
        for (AssegnazioneTurno entity : turni) {
            assegnazioneTurnoDTOS.add(assegnazioneTurnoToDTO(entity));
        }
        return assegnazioneTurnoDTOS;
    }



}

