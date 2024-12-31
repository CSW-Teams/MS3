package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.specialization.ISpecializationController;
import org.cswteams.ms3.dto.specializations.SpecializationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specializations")
public class SpecializationRestEndPoint {
    @Autowired
    private ISpecializationController specializationController;

    @PreAuthorize("hasAnyRole('CONFIGURATOR', 'DOCTOR', 'PLANNER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllUsers() {
        SpecializationDTO specializationDTO;
        try {
            specializationDTO = specializationController.getAllSpecializations();
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (specializationDTO == null) {
            return new ResponseEntity<>(specializationDTO, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(specializationDTO, HttpStatus.OK);
    }
}
