package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;

import java.util.ArrayList;
import java.util.List;

public class MappaRichiestaRimozioneDaTurno {

    public static RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoToDTO(RichiestaRimozioneDaTurno richiestaRimozioneDaTurno) {
        return new RichiestaRimozioneDaTurnoDTO(
                richiestaRimozioneDaTurno.getAssegnazioneTurno().getId(),
                richiestaRimozioneDaTurno.getUtente().getNome(),
                richiestaRimozioneDaTurno.getUtente().getCognome(),
                ConvertitoreData.daStandardVersoTestuale(richiestaRimozioneDaTurno.getUtente().getDataNascita().toString()),
                richiestaRimozioneDaTurno.getUtente().getRuoloEnum(),
                richiestaRimozioneDaTurno.getUtente().getStato(),
                richiestaRimozioneDaTurno.getUtente().getSpecializzazioni(),
                richiestaRimozioneDaTurno.getDescrizione());
    }

    public static List<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoEntitytoDTO(List<RichiestaRimozioneDaTurno> richiestaRimozioneDaTurnoList) {
        List<RichiestaRimozioneDaTurnoDTO> richiestaRimozioneDaTurnoDTOS = new ArrayList<>();
        for (RichiestaRimozioneDaTurno entity : richiestaRimozioneDaTurnoList) {
            richiestaRimozioneDaTurnoDTOS.add(richiestaRimozioneDaTurnoToDTO(entity));
        }
        return richiestaRimozioneDaTurnoDTOS;
    }
}
