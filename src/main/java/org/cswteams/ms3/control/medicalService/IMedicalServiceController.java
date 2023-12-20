package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dto.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface IMedicalServiceController {

    Set<MedicalServiceDTO> leggiServizi();
    MedicalServiceDTO leggiServizioByNome(@NotNull String nome);
    MedicalService creaServizio(@NotNull MedicalServiceDTO servizio);
}
