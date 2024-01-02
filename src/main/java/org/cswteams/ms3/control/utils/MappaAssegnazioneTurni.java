package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class MappaAssegnazioneTurni {


  /*  public static AssegnazioneTurno assegnazioneTurnoDTOToEntity(AssegnazioneTurnoDTO dto) throws TurnoException {
        // FIXME: DA SISTEMARE, DEVE PRENDERLO DAL DB
        Turno turno = new Turno(dto.getIdTurno(), dto.getInizio().toLocalDateTime().toLocalTime(), dto.getFine().toLocalDateTime().toLocalTime(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getTipologiaTurno(),dto.isGiornoSuccessivoTurno());
        Set<Utente> diGuardia = MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia());
        Set<Utente> reperibili = MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili());
        turno.setMansione(dto.getMansione());

        return new AssegnazioneTurno(dto.getInizio().toLocalDateTime().toLocalDate(), turno, reperibili, diGuardia);
    }*/

    public static AssegnazioneTurnoDTO assegnazioneTurnoToDTO(AssegnazioneTurno entity) {
        ZoneId gmtZone = ZoneId.of("GMT");

        LocalDateTime localDateTimeInizio = LocalDateTime.of(entity.getData(), entity.getTurno().getOraInizio());
        Instant inizioInstant = localDateTimeInizio.atZone(gmtZone).toInstant();

        Duration durata = entity.getTurno().getDurata();
        Instant fineInstant = inizioInstant.plus(durata);

        long inizioEpoch = inizioInstant.getEpochSecond();
        long fineEpoch = fineInstant.getEpochSecond();

        Set<UtenteDTO> diGuardiaDto = MappaUtenti.utentiEntitytoDTO(entity.getUtentiDiGuardia());
        Set<UtenteDTO> reperibiliDto = MappaUtenti.utentiEntitytoDTO(entity.getUtentiReperibili());
        Set<UtenteDTO> rimossiDto = MappaUtenti.utentiEntitytoDTO(entity.getRetiredUsers());

        AssegnazioneTurnoDTO dto = new AssegnazioneTurnoDTO(
                entity.getId(),
                entity.getTurno().getId(),
                inizioEpoch,
                fineEpoch,
                diGuardiaDto,
                reperibiliDto,
                MappaServizio.servizioEntitytoDTO(entity.getTurno().getServizio()),
                entity.getTurno().getTipologiaTurno(),
                entity.getTurno().isReperibilitaAttiva()
        );
        dto.setMansione(entity.getTurno().getMansione());
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

