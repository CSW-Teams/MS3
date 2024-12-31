package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.vincoli.IConstraintController;
import org.cswteams.ms3.dto.ConfigConstraintDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/constraints/")
public class ConstraintRestEndpoint {
    @Autowired
    IConstraintController constraintController;

    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readConstraints()  {
        return new ResponseEntity<>(constraintController.readConstraints(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET, path = "configuration")
    public ResponseEntity<?> readConstraintsConfiguration()  {
        return new ResponseEntity<>(constraintController.readConfigConstraints(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.POST, path = "configuration")
    public ResponseEntity<?> updateConstraintsConfiguration(@RequestBody() @Valid @Validated ConfigConstraintDTO constraintDTO) {
        if (constraintDTO != null) {
            return new ResponseEntity<>(constraintController.updateConstraints(constraintDTO), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}