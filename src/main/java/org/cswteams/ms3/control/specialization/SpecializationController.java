package org.cswteams.ms3.control.specialization;

import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dto.specializations.SpecializationDTO;
import org.cswteams.ms3.entity.Specialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
public class SpecializationController implements ISpecializationController{

    @Autowired
    private SpecializationDAO specializationDAO;

    @Override
    public SpecializationDTO getAllSpecializations() {
        List<Specialization> specializationList = specializationDAO.findAll();
        SpecializationDTO specializationDTO = new SpecializationDTO(new HashSet<>());
        boolean flag;


        for(Specialization specialization : specializationList){
            flag = false;
            for(String spec : specializationDTO.getSpecializations()){
                if(Objects.equals(spec, specialization.getType())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                specializationDTO.getSpecializations().add(specialization.getType());
            }

        }
        return specializationDTO;
    }
}
