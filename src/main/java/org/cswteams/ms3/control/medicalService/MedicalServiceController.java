package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dao.MedicalServiceDAO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Service
public class MedicalServiceController implements IMedicalServiceController {


    @Autowired
    MedicalServiceDAO medicalServiceDAO;

    @Override
    public Set<MedicalServiceDTO> getAllMedicalServices() {
        //List<MedicalService> medicalServiceList = medicalServiceDAO.findAll();
        Set<MedicalServiceDTO> medicalServiceDTOList = new HashSet<>();
        return medicalServiceDTOList;
    }

    @Override
    public MedicalServiceDTO leggiServizioByNome(@NotNull String nome) {
        //return MappaServizio.servizioEntitytoDTO(serviceDAO.findByNome(nome));
        return null;
    }

    @Override
    public MedicalService creaServizio(@NotNull MedicalServiceDTO servizio) {
        //return serviceDAO.save(MappaServizio.servizioDTOtoEntity(servizio));
        return null;
    }
}
