package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dto.medicalservice.AvailableTasksTypesDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceCreationDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceWithTaskAssignmentsDTO;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.exception.DatabaseException;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public interface IMedicalServiceController {

    /**
     * Create a new <i>Medical service</i>, based on data within the DTO received via frontend.
     *
     * @param medicalServiceDTO DTO received via frontend
     * @return a new <code>MedicalService</code> object, according to DTO input data
     */
    MedicalService createService(@NotNull MedicalServiceCreationDTO medicalServiceDTO);

    /**
     * Create a new <i>Medical service</i>, based on label and associated <i>tasks</i> list.
     *
     * @param taskList tasks to be associated with the new <i>Medical service</i>
     * @param label    label to be assigned to the new <i>Medical service</i>
     * @return a new <code>MedicalService</code> object, according to input data
     */
    MedicalService createService(List<Task> taskList, String label);

    Set<MedicalServiceWithTaskAssignmentsDTO> getAllMedicalServices();

    MedicalServiceDTO getServiceByName(@NotNull String serviceName);

    MedicalServiceDTO updateService(@NotNull MedicalServiceDTO medicalServiceDTO) throws DatabaseException, RuntimeException;

    boolean deleteService(@NotNull Long serviceId) throws DatabaseException;

    /**
     * Generate a DTO containing all the available <i>task</i> types that can be assigned to a <i>Medical service</i>.
     *
     * @return DTO with available <i>task</i> types
     * @see org.cswteams.ms3.enums.TaskEnum
     */
    AvailableTasksTypesDTO getAvailableTaskTypes();
}
