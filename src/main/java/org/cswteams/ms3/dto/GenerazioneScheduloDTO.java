package org.cswteams.ms3.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GenerazioneScheduloDTO {
    private int giornoInizio;
    private int meseInizio;
    private int annoInizio;
    private int giornoFine;
    private int meseFine;
    private int annoFine;

    public LocalDate getStartDate(){
        return LocalDate.of(annoInizio,meseInizio,giornoInizio);
    }

    public LocalDate getEndDate(){
        return LocalDate.of(annoFine,meseFine,giornoFine);
    }
}
