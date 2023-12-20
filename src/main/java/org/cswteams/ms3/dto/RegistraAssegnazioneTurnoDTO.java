package org.cswteams.ms3.dto;

import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;
import org.cswteams.ms3.enums.TipologiaTurno;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RegistraAssegnazioneTurnoDTO {

    private LocalDate giorno;

    private TipologiaTurno tipologiaTurno;

    private MansioneEnum mansione;
    private Set<DoctorDTO> utentiDiGuardia;
    private Set<DoctorDTO> utentiReperibili;
    private ServizioDTO servizio;
    private boolean forced;

    public RegistraAssegnazioneTurnoDTO(){}


}
