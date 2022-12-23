package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.TurnoException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaAssegnazioneTurni {

    public static AssegnazioneTurno assegnazioneTurnoDTOToEntity(AssegnazioneTurnoDTO dto) {
        Turno turno = new Turno(dto.getIdTurno(),dto.getInizio().toLocalDateTime().toLocalTime(),dto.getFine().toLocalDateTime().toLocalTime(),MappaServizio.servizioDTOtoEntity(dto.getServizio()),dto.getTipologiaTurno());
        Set<Utente> diGuardia = MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia());
        Set<Utente> reperibili = MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili());

        return new AssegnazioneTurno(dto.getInizio().toLocalDateTime().toLocalDate(), turno, reperibili, diGuardia);
    }

    public static AssegnazioneTurnoDTO assegnazioneTurnoToDTO(AssegnazioneTurno entity) {
        LocalDateTime inizio = LocalDateTime.of(entity.getDate(), entity.getTurno().getOraInizio());
        LocalDateTime fine = LocalDateTime.of(entity.getDate(), entity.getTurno().getOraFine());

        Instant instantinizio = inizio.atZone(ZoneId.systemDefault()).toInstant();
        Instant instantfine = fine.atZone(ZoneId.systemDefault()).toInstant();

        Date dateinizio= Date.from(instantinizio);
        Date datefine = Date.from(instantfine);

        Timestamp timestampInizio = new Timestamp(dateinizio.getTime());
        Timestamp timestampFine = new Timestamp(datefine.getTime());
        Set<UtenteDTO> diGuardiaDto = MappaUtenti.utenteEntitytoDTO(entity.getUtentiDiGuardia());
        Set<UtenteDTO> reperibiliDto = MappaUtenti.utenteEntitytoDTO(entity.getUtentiReperibili());
        AssegnazioneTurnoDTO dto = new AssegnazioneTurnoDTO(entity.getId(),entity.getTurno().getId(),timestampInizio,timestampFine,diGuardiaDto,reperibiliDto,MappaServizio.servizioEntitytoDTO(entity.getTurno().getServizio()), entity.getTurno().getTipologiaTurno());
        return dto;
    }

    public static Set<AssegnazioneTurnoDTO> assegnazioneTurnoToDTO(Set<AssegnazioneTurno> turni) {
        Set<AssegnazioneTurnoDTO> assegnazioneTurnoDTOS = new HashSet<>();
        for (AssegnazioneTurno entity: turni){
            assegnazioneTurnoDTOS.add(assegnazioneTurnoToDTO(entity));
        }
        return assegnazioneTurnoDTOS;
    }

    public static Set<AssegnazioneTurnoDTO> assegnazioneTurnoToDTO(List<AssegnazioneTurno> turni) {
        Set<AssegnazioneTurnoDTO> assegnazioneTurnoDTOS = new HashSet<>();
        for (AssegnazioneTurno entity: turni){
            assegnazioneTurnoDTOS.add(assegnazioneTurnoToDTO(entity));
        }
        return assegnazioneTurnoDTOS;
    }
}
