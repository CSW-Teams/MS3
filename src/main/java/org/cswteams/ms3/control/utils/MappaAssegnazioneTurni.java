package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.entity.ConcreteShift;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class MappaAssegnazioneTurni {


  /*  public static ConcreteShift assegnazioneTurnoDTOToEntity(AssegnazioneTurnoDTO dto) throws TurnoException {
        // FIXME: DA SISTEMARE, DEVE PRENDERLO DAL DB
        Shift turno = new Shift(dto.getIdTurno(), dto.getInizio().toLocalDateTime().toLocalTime(), dto.getFine().toLocalDateTime().toLocalTime(), MappaServizio.servizioDTOtoEntity(dto.getServizio()), dto.getTipologiaTurno(),dto.isGiornoSuccessivoTurno());
        Set<Utente> diGuardia = MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia());
        Set<Utente> reperibili = MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili());
        turno.setMansione(dto.getMansione());

        return new ConcreteShift(dto.getInizio().toLocalDateTime().toLocalDate(), turno, reperibili, diGuardia);
    }*/

    public static AssegnazioneTurnoDTO assegnazioneTurnoToDTO(AssegnazioneTurno entity) {
        ZoneId gmtZone = ZoneId.of("GMT");

        LocalDateTime localDateTimeInizio = LocalDateTime.of(entity.getData(), entity.getTurno().getOraInizio());
        Instant inizioInstant = localDateTimeInizio.atZone(gmtZone).toInstant();

        Duration durata = entity.getTurno().getDurata();
        Instant fineInstant = inizioInstant.plus(durata);

        long inizioEpoch = inizioInstant.getEpochSecond();
        long fineEpoch = fineInstant.getEpochSecond();

        Set<DoctorDTO> diGuardiaDto = MappaUtenti.utentiEntityToDTO(entity.getUtentiDiGuardia());
        Set<DoctorDTO> reperibiliDto = MappaUtenti.utentiEntityToDTO(entity.getUtentiReperibili());
        Set<DoctorDTO> rimossiDto = MappaUtenti.utentiEntityToDTO(entity.getRetiredDoctors());

        AssegnazioneTurnoDTO dto = new AssegnazioneTurnoDTO(
                entity.getId(),
                entity.getShift().getId(),
                inizioEpoch,
                fineEpoch,
                diGuardiaDto,
                reperibiliDto,
                MappaServizio.servizioEntitytoDTO(entity.getShift().getServizio()),
                entity.getShift().getTipologiaTurno(),
                entity.getShift().isReperibilitaAttiva()
        );
        dto.setMansione(entity.getShift).getMansione());
        dto.setRetiredUsers(rimossiDto);

        return dto;
    }

    public static Set<AssegnazioneTurnoDTO> assegnazioneTurnoToDTO(Set<ConcreteShift> turni) {
        Set<AssegnazioneTurnoDTO> assegnazioneTurnoDTOS = new HashSet<>();
        for (ConcreteShift entity : turni) {
            assegnazioneTurnoDTOS.add(assegnazioneTurnoToDTO(entity));
        }
        return assegnazioneTurnoDTOS;
    }



}

