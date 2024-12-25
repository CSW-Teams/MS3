package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.conditions.IConditionController;
import org.cswteams.ms3.dto.condition.AllSavedConditionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conditions")
@PreAuthorize("hasAnyRole('CONFIGURATOR', 'DOCTOR', 'PLANNER')")
public class AllSavedConditionsRestEndPoint {
    @Autowired
    private IConditionController conditionController;

    @PreAuthorize("hasAnyAuthority('configurator:get', 'doctor:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllSavedConditions() {
        AllSavedConditionDTO allSavedConditionDTO;
        try {
            allSavedConditionDTO = conditionController.getAllSavedConditions();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(allSavedConditionDTO == null){
            return new ResponseEntity<>(allSavedConditionDTO, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(allSavedConditionDTO, HttpStatus.OK);
    }
}

