package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dto.medicalservice.AvailableTasksTypesDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;

import javax.validation.constraints.NotNull;
import java.util.Set;

public interface IMedicalServiceController {

    MedicalService createService(@NotNull MedicalServiceDTO medicalServiceDTO);
    Set<MedicalServiceDTO> getAllMedicalServices();
    MedicalServiceDTO getServiceByName(@NotNull String serviceName);
    AvailableTasksTypesDTO getAvailableTaskTypes();
}
