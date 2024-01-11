package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.control.task.ITaskController;
import org.cswteams.ms3.dao.MedicalServiceDAO;
import org.cswteams.ms3.dto.medicalservice.AvailableTasksTypesDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceCreationDTO;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class MedicalServiceController implements IMedicalServiceController {

    @Autowired
    MedicalServiceDAO medicalServiceDAO;

    @Autowired
    ITaskController taskController;

    @Override
    public MedicalService createService(@NotNull MedicalServiceCreationDTO medicalServiceCreationDTO) {
        List<Task> taskList = new ArrayList<>();
        for (String taskTypeString : medicalServiceCreationDTO.getTaskTypes()) {
            Task t = taskController.createTask(TaskEnum.valueOf(taskTypeString));
            taskList.add(t);
        }
        return this.createService(taskList, medicalServiceCreationDTO.getName());
    }

    @Override
    public MedicalService createService(List<Task> taskList, String label) {
        String labelUpper = label.toUpperCase();
        // check if not already existent
        MedicalService retrieved = medicalServiceDAO.findByLabel(labelUpper);
        if (retrieved == null) {
            MedicalService newService = new MedicalService(taskList, labelUpper);
            return medicalServiceDAO.saveAndFlush(newService);
        } else {
            retrieved.addTasks(taskList);
            medicalServiceDAO.saveAndFlush(retrieved);
            return retrieved;
        }
    }

    @Override
    public Set<MedicalServiceDTO> getAllMedicalServices() {
        List<MedicalService> medicalServiceList = medicalServiceDAO.findAll();
        return buildDTOList(medicalServiceList);
    }

    @Override
    public MedicalServiceDTO getServiceByName(@NotNull String serviceName) {
        MedicalService medicalService = medicalServiceDAO.findByLabel(serviceName);
        return buildDTO(medicalService);
    }

    @Override
    public MedicalServiceDTO updateService(@NotNull MedicalServiceDTO medicalServiceDTO) throws DatabaseException {
        Optional<MedicalService> medicalServiceOpt = medicalServiceDAO.findById(medicalServiceDTO.getId());
        if (medicalServiceOpt.isEmpty()) {
            throw new DatabaseException("MedicalService not found for id = " + medicalServiceDTO.getId());
        }
        MedicalService medicalService = medicalServiceOpt.get();
        medicalService.setLabel(medicalServiceDTO.getNome());
        medicalService.setTasks(medicalServiceDTO.getMansioni());
        medicalServiceDAO.saveAndFlush(medicalService);
        return medicalServiceDTO;
    }

    @Override
    public boolean deleteService(@NotNull MedicalServiceDTO medicalServiceDTO) throws DatabaseException {
        Optional<MedicalService> medicalServiceOpt = medicalServiceDAO.findById(medicalServiceDTO.getId());
        if (medicalServiceOpt.isEmpty()) {
            throw new DatabaseException("MedicalService not found for id = " + medicalServiceDTO.getId());
        }
        MedicalService medicalService = medicalServiceOpt.get();
        medicalServiceDAO.delete(medicalService);
        return true;
    }

    @Override
    public AvailableTasksTypesDTO getAvailableTaskTypes() {
        return new AvailableTasksTypesDTO();
    }

    private MedicalServiceDTO buildDTO(MedicalService medicalService) {
        return new MedicalServiceDTO(
                medicalService.getId(),
                medicalService.getLabel(),
                medicalService.getTasks());
    }

    private Set<MedicalServiceDTO> buildDTOList(List<MedicalService> medicalServiceList) {
        Set<MedicalServiceDTO> medicalServiceDTOS = new HashSet<>();
        for (MedicalService entity : medicalServiceList) {
            medicalServiceDTOS.add(buildDTO(entity));
        }
        return medicalServiceDTOS;
    }
}
