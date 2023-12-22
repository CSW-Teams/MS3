package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaRichiestaRimozioneDaTurno {
    // TODO: Use logic in controller to convert, mappers like this aren't the best way to operate
/*
    public static RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoToDTO(RichiestaRimozioneDaTurno richiestaRimozioneDaTurno) {
        return new RichiestaRimozioneDaTurnoDTO(
                richiestaRimozioneDaTurno.getIdRichiestaRimozioneDaTurno(),
                richiestaRimozioneDaTurno.getAssegnazioneTurno().getId(),
                richiestaRimozioneDaTurno.getUtenteRichiedente().getId(),
                richiestaRimozioneDaTurno.getUtenteSostituto() == null ? null : richiestaRimozioneDaTurno.getUtenteSostituto().getId(),
                richiestaRimozioneDaTurno.isEsito(),
                richiestaRimozioneDaTurno.getDescrizione(),
                richiestaRimozioneDaTurno.getAllegato(),
                richiestaRimozioneDaTurno.isEsaminata());
    }*/
/*
    public static Set<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoEntitytoDTO(List<RichiestaRimozioneDaTurno> richiestaRimozioneDaTurnoList) {
        Set<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoDTOS = new HashSet<>();
        for (RichiestaRimozioneDaTurno entity : richiestaRimozioneDaTurnoList) {
            richiestaRimozioneDaTurnoDTOS.add(richiestaRimozioneDaTurnoToDTO(entity));
        }
        return richiestaRimozioneDaTurnoDTOS;
    }*/
}
