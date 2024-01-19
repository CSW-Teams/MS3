package org.cswteams.ms3.control.specialization;

import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dto.category.SpecializationDTO;
import org.cswteams.ms3.entity.Specialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class SpecializationController implements ISpecializationController{

    @Autowired
    private SpecializationDAO specializationDAO;

    @Override
    public SpecializationDTO getAllSpecializations() {
        List<Specialization> specializationList = specializationDAO.findAll();
        SpecializationDTO specializationDTO = new SpecializationDTO(new HashSet<>());

        for(Specialization specialization : specializationList){
            specializationDTO.getSpecializations().add(specialization.getType());
        }
        return specializationDTO;
    }
}
