package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dao.MedicalServiceDAO;
import org.cswteams.ms3.dto.RequestRemovalFromConcreteShiftDTO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;
import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MedicalServiceController implements IMedicalServiceController {

    @Autowired
    MedicalServiceDAO medicalServiceDAO;

    @Override
    public Set<MedicalServiceDTO> getAllMedicalServices() {
        List<MedicalService> medicalServiceList = medicalServiceDAO.findAll();
        return buildDTOList(medicalServiceList);
    }

    @Override
    public MedicalServiceDTO leggiServizioByNome(@NotNull String nome) {
        MedicalService medicalService=medicalServiceDAO.findByLabel(nome);
        return buildDTO(medicalService);
    }

    @Override
    public MedicalService creaServizio(@NotNull MedicalServiceDTO medicalServiceDTO) {
        return medicalServiceDAO.save(new MedicalService(medicalServiceDTO.getMansioni(),medicalServiceDTO.getNome()));
    }

    private MedicalServiceDTO buildDTO(MedicalService medicalService) {
        return new MedicalServiceDTO(
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
