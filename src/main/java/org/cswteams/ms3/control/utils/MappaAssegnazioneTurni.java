package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Utente;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class MappaAssegnazioneTurni {

    public static AssegnazioneTurno assegnazioneTurnoDTOToEntity(AssegnazioneTurnoDTO dto) {
        return null;
    }

    public static AssegnazioneTurnoDTO assegnazioneTurnoToDTO(AssegnazioneTurno entity) {
        Timestamp inizio = Timestamp.valueOf(LocalDateTime.of(entity.getDate(), entity.getTurno().getOraInizio()));
        Timestamp fine = Timestamp.valueOf(LocalDateTime.of(entity.getDate(), entity.getTurno().getOraFine()));
        Set<UtenteDTO> diGuardiaDto = MappaUtenti.utenteENTITYtoDTO(entity.getUtentiDiGuardia());
        Set<UtenteDTO> reperibiliDto = MappaUtenti.utenteENTITYtoDTO(entity.getUtentiReperibili());
        AssegnazioneTurnoDTO dto = new AssegnazioneTurnoDTO(entity.getId(),inizio,fine,diGuardiaDto,reperibiliDto,entity.getTurno().getServizio(), entity.getTurno().getTipologiaTurno());
        return dto;
    }

    public static Set<AssegnazioneTurnoDTO> assegnazioneTurnoToDTO(Set<AssegnazioneTurno> turni){
        Set<AssegnazioneTurnoDTO> assegnazioneTurnoDTOS = new HashSet<>();
        for (AssegnazioneTurno entity: turni){
            assegnazioneTurnoDTOS.add(assegnazioneTurnoToDTO(entity));
        }
        return assegnazioneTurnoDTOS;
    }
}
