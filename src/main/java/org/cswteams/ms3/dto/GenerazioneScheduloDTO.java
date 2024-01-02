package org.cswteams.ms3.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GenerazioneScheduloDTO {
    private LocalDate giornoInizio;
    private LocalDate giornoFine;
}
