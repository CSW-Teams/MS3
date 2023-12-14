package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaRichiestaRimozioneDaTurno {

    public static RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoToDTO(RichiestaRimozioneDaTurno richiestaRimozioneDaTurno) {
        return new RichiestaRimozioneDaTurnoDTO(
                richiestaRimozioneDaTurno.getId(),
                richiestaRimozioneDaTurno.getAssegnazioneTurno().getId(),
                richiestaRimozioneDaTurno.getUtente().getId(),
                richiestaRimozioneDaTurno.isEsito(),
                richiestaRimozioneDaTurno.getDescrizione(),
                richiestaRimozioneDaTurno.getAllegato());
    }

    public static Set<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoEntitytoDTO(List<RichiestaRimozioneDaTurno> richiestaRimozioneDaTurnoList) {
        Set<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoDTOS = new HashSet<>();
        for (RichiestaRimozioneDaTurno entity : richiestaRimozioneDaTurnoList) {
            richiestaRimozioneDaTurnoDTOS.add(richiestaRimozioneDaTurnoToDTO(entity));
        }
        return richiestaRimozioneDaTurnoDTOS;
    }
}
