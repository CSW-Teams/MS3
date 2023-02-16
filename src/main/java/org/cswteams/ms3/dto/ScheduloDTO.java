package org.cswteams.ms3.dto;

import lombok.Data;

@Data
public class ScheduloDTO {

    private String dataInizio;
    private String dataFine;
    private long id;
    private boolean illegalita;

    public ScheduloDTO(String dataInizio, String dataFine, boolean illegalita, long id) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.illegalita = illegalita;
        this.id = id;
    }

    public ScheduloDTO() {
    }


}
