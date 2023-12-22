package org.cswteams.ms3.control.condition;

import org.cswteams.ms3.dao.PermanentConditionDAO;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dao.TemporaryConditionDAO;
import org.cswteams.ms3.dto.category.PermanentConditionDTO;
import org.cswteams.ms3.dto.category.SpecializationDTO;
import org.cswteams.ms3.dto.category.TemporaryConditionDTO;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ConditionController implements IConditionController {


    @Autowired
    private PermanentConditionDAO permanentConditionDao;
    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO;
    @Autowired
    private SpecializationDAO specializationDao;


    @Override
    public Set<PermanentConditionDTO> readPermanentConditions() {
        List<PermanentCondition> permanentConditionList = permanentConditionDao.findAll();
        Set<PermanentConditionDTO> permanentConditionDTOSet = new HashSet<>();

        for(PermanentCondition permanentCondition : permanentConditionList){
            permanentConditionDTOSet.add(new PermanentConditionDTO(permanentCondition.getType()));
        }
        return permanentConditionDTOSet;
    }

    @Override
    public Set<TemporaryConditionDTO> readTemporaryConditions() {
        List<TemporaryCondition> temporaryConditionList = temporaryConditionDAO.findAll();
        Set<TemporaryConditionDTO> temporaryConditionDTOSet = new HashSet<>();

        for(TemporaryCondition temporaryCondition : temporaryConditionList){
            temporaryConditionDTOSet.add(new TemporaryConditionDTO(temporaryCondition.getType(),temporaryCondition.getStartDate(),temporaryCondition.getEndDate()));
        }
        return temporaryConditionDTOSet;
    }

    @Override
    public Set<SpecializationDTO> readSpecializations() {
        List<Specialization> specializationList = specializationDao.findAll();
        Set<SpecializationDTO> specializationDTOSet = new HashSet<>();

        for(Specialization specialization : specializationList){
            specializationDTOSet.add(new SpecializationDTO(specialization.getType()));
        }
        return specializationDTOSet;
    }

}
