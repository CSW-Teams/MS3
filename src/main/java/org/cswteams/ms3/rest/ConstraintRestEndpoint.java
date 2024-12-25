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
@PreAuthorize("hasAnyRole('CONFIGURATOR', 'PLANNER')")
public class ConstraintRestEndpoint {
    @Autowired
    IConstraintController constraintController;

    @PreAuthorize("hasAuthority('planner:get')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readConstraints()  {
        return new ResponseEntity<>(constraintController.readConstraints(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasAuthority('configurator:get')")
    @RequestMapping(method = RequestMethod.GET, path = "configuration")
    public ResponseEntity<?> readConstraintsConfiguration()  {
        return new ResponseEntity<>(constraintController.readConfigConstraints(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasAuthority('configurator:post')")
    @RequestMapping(method = RequestMethod.POST, path = "configuration")
    public ResponseEntity<?> updateConstraintsConfiguration(@RequestBody() @Valid @Validated ConfigConstraintDTO constraintDTO) {
        if (constraintDTO != null) {
            return new ResponseEntity<>(constraintController.updateConstraints(constraintDTO), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}