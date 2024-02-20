package org.cswteams.ms3.control.conditions;

import org.cswteams.ms3.dao.PermanentConditionDAO;
import org.cswteams.ms3.dao.TemporaryConditionDAO;
import org.cswteams.ms3.dto.condition.AllSavedConditionDTO;
import org.cswteams.ms3.dto.condition.TemporaryConditionDTO;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ConditionController implements IConditionController{
    @Autowired
    private PermanentConditionDAO permanentConditionDAO;
    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO;
    @Override
    public AllSavedConditionDTO getAllSavedConditions() {
        List<PermanentCondition> permanentConditionList = permanentConditionDAO.findAll();
        List<TemporaryCondition> temporaryConditionsList = temporaryConditionDAO.findAll();
        boolean flag;

        AllSavedConditionDTO allSavedConditionDTO = new AllSavedConditionDTO(new HashSet<>());

        for(PermanentCondition permanentCondition : permanentConditionList){
            flag = false;
            for(AllSavedConditionDTO.SingleSavedCondition cond : allSavedConditionDTO.getAllSavedConditions()){
                if(cond.getLabel().equals(permanentCondition.getType())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                allSavedConditionDTO.getAllSavedConditions().add(
                        new AllSavedConditionDTO.SingleSavedCondition(
                                permanentCondition.getId(),
                                permanentCondition.getType(),
                                0L,
                                0L,
                                true
                        )
                );
            }

        }

        for(TemporaryCondition temporaryCondition : temporaryConditionsList){
            flag = false;
            for(AllSavedConditionDTO.SingleSavedCondition cond : allSavedConditionDTO.getAllSavedConditions()){
                if(cond.getLabel().equals(temporaryCondition.getType())){
                    flag = true;
                    break;
                }
            }
            if(!flag){
                allSavedConditionDTO.getAllSavedConditions().add(
                        new AllSavedConditionDTO.SingleSavedCondition(
                                temporaryCondition.getId(),
                                temporaryCondition.getType(),
                                temporaryCondition.getStartDate(),
                                temporaryCondition.getEndDate(),
                                false
                        )
                );
            }

        }
        return allSavedConditionDTO;
    }
}
