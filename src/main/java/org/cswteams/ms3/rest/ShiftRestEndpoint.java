package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.shift.IShiftController;
import org.cswteams.ms3.dto.shift.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoint for api/shifts
 */
@RestController
@RequestMapping("/shifts/")
public class ShiftRestEndpoint {

    @Autowired
    IShiftController shiftController;

    /**
     * Retrieves all shift definitions <br/>
     * Reached from <b>GET api/shifts</b>
     * @return A response containing a list of {@link org.cswteams.ms3.dto.shift.ShiftDTOOut}
     */
    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readAllShifts() {
        List<ShiftDTOOut> allShifts = shiftController.getAllShifts();
        return new ResponseEntity<>(allShifts, HttpStatus.OK);
    }

    /**
     * Soft delete a shift <br/>
     * Reached from <b>DELETE api/shifts/{id}</b>
     * @param id The ID of the shift to soft delete
     * @return A response indicating the outcome
     */
    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.DELETE, path = "{id}")
    public ResponseEntity<?> deleteShift(@PathVariable Long id) {
        try {
            shiftController.deleteShift(id); // Chiama il controller per eseguire la soft delete
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves all shift definitions <br/>
     * Reached from <b> GET api/shifts/service={serviceName}</b>
     * @param serviceName A string describing the service to use
     * @return The shift definitions relative to the specified service
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.GET, path = "/service={serviceName}")
    public ResponseEntity<?> readShiftsByServiceName(@PathVariable String serviceName) {
        if (serviceName != null) {
            List<ShiftDTOOut> shifts = shiftController.getShiftsOfService(new ShiftServiceNameDTOIn(serviceName));
            if (shifts == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(shifts, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a shift definition <br/>
     * Reached from <b> POST api/shifts/service={serviceName}</b>
     * @param shift A DTO containing all the shift's information, in request body
     * @return A response containing a {@link org.cswteams.ms3.dto.shift.ShiftDTOOut}
     */
    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<?> createShift(@RequestBody() ShiftDTOIn shift) {

        if (shift != null) {
            try {
                return new ResponseEntity<>(shiftController.createShift(shift), HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyRole('CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET, path = "constants")
    public ResponseEntity<?> createShift() {
        return new ResponseEntity<>(new ShiftConstantsDTO(), HttpStatus.FOUND);
    }
}
