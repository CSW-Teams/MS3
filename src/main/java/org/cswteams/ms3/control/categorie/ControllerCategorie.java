package org.cswteams.ms3.control.categorie;

import org.cswteams.ms3.dao.PermanentConditionDao;
import org.cswteams.ms3.dao.RotationDao;
import org.cswteams.ms3.dao.SpecializationDao;
import org.cswteams.ms3.dao.TemporaryConditionDAO;
import org.cswteams.ms3.dto.category.*;
import org.cswteams.ms3.entity.category.PermanentCondition;
import org.cswteams.ms3.entity.category.Rotation;
import org.cswteams.ms3.entity.category.Specialization;
import org.cswteams.ms3.entity.category.TemporaryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ControllerCategorie implements IControllerCategorie {

    @Autowired
    PermanentConditionDao permanentConditionDao;
    @Autowired
    TemporaryConditionDAO temporaryConditionDAO;
    @Autowired
    SpecializationDao specializationDao;
    @Autowired
    RotationDao rotationDao;

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

    @Override
    public Set<RotationDTO> readRotations() {
        List<Rotation> rotationList = rotationDao.findAll();
        Set<RotationDTO> rotationDTOSet = new HashSet<>();

        for(Rotation rotation : rotationList){
            rotationDTOSet.add(new RotationDTO(rotation.getType()));
        }

        return rotationDTOSet;
    }
}
