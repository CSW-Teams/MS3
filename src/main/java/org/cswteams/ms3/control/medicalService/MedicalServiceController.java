package org.cswteams.ms3.control.medicalService;

import org.cswteams.ms3.dao.MedicalServiceDAO;
import org.cswteams.ms3.dto.MedicalServiceDTO;
import org.cswteams.ms3.entity.MedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Service
public class MedicalServiceController implements IMedicalServiceController {


    @Autowired
    MedicalServiceDAO medicalServiceDAO;

    @Override
    public Set<MedicalServiceDTO> leggiServizi() {
        //return MappaServizio.servizioEntitytoDTO(serviceDAO.findAll());
        return null;
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
