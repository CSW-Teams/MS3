package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.dto.ScheduleGenerationDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/")
public class ScheduleRestEndpoint {

    @Autowired
    private ISchedulerController schedulerController;

    /*
     * This method is invoked by the frontend to request a new shift schedule in the range of
     * dates passed as parameters.
     */
    @RequestMapping(method = RequestMethod.POST, path = "generation")
    public ResponseEntity<?> createSchedule(@RequestBody() ScheduleGenerationDTO gs) {
        if (gs != null) {

            //Only the requests with admissible dates will be considered.
            if(gs.getStartDate().isBefore(gs.getEndDate())){

                //The request is passed to the controller.
                Schedule schedule = schedulerController.createSchedule(gs.getStartDate(),gs.getEndDate());
                if(schedule == null)
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                if(!schedule.getViolatedConstraints().isEmpty())
                    return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                else
                    return new ResponseEntity<>(HttpStatus.ACCEPTED);

            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /*
     * This method is invoked by the frontend to request a regeneration of an existing shift schedule.
     */
    @RequestMapping(method = RequestMethod.POST, path = "regeneration/id={id}")
    public ResponseEntity<?> recreateSchedule(@PathVariable Long id) {
        if (id != null) {

            try {
                if(schedulerController.recreateSchedule(id))
                    return new ResponseEntity<>(HttpStatus.ACCEPTED);
                else
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

            } catch (UnableToBuildScheduleException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /*
     * This method is invoked to retrieve all the existing shift schedules.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readSchedules()  {

        List<ScheduleDTO> set = schedulerController.readSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }

    /*
     * This method is invoked to retrieve the illegal shift schedules.
     */
    @RequestMapping(method = RequestMethod.GET,path = "illegals")
    public ResponseEntity<?> readIllegalSchedules()  {

        List<ScheduleDTO> set = schedulerController.readIllegalSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }

    /*
     * This method is invoked to delete an existing shift schedule.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "id={id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id)  {

        if (id != null ) {
            //It is not possible to delete a schedule in the past. This check is made by the controller.
            if(schedulerController.removeSchedule(id))
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
