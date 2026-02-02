package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.scheduler.ISchedulerController;
import org.cswteams.ms3.dto.ScheduleGenerationDTO;
import org.cswteams.ms3.dto.ScheduleDTO;
import org.cswteams.ms3.dto.showscheduletoplanner.ShowScheduleToPlannerDTO;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.exception.UnableToBuildScheduleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/schedule/")
public class ScheduleRestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleRestEndpoint.class);
    private static final String MODE_KEY = "planMode";
    private static final String NEW_PLAN_ID_KEY = "newPlanId";

    @Autowired
    private ISchedulerController schedulerController;

    /*
     * This method is invoked by the frontend to request a new shift schedule in the range of
     * dates passed as parameters.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "generation")
    public ResponseEntity<?> createSchedule(@RequestBody() ScheduleGenerationDTO gs) {
        long startTime = System.currentTimeMillis();
        MDC.put(MODE_KEY, "generate");
        Map<String, Object> startFields = new LinkedHashMap<>();
        startFields.put("startDate", gs != null ? gs.getStartDate() : null);
        startFields.put("endDate", gs != null ? gs.getEndDate() : null);
        logEvent("plan_generate_start", "generate", startFields);
        try {
            if (gs != null) {
                //Only the requests with admissible dates will be considered.
                if(!gs.getEndDate().isBefore(gs.getStartDate())){

                    //The request is passed to the controller.
                    Schedule schedule = schedulerController.createSchedule(gs.getStartDate(),gs.getEndDate());
                    if(schedule == null) {
                        logEvent("plan_generate_failed", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "errorType", "NOT_ACCEPTABLE"
                        ));
                        return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                    }
                    if(!schedule.getViolatedConstraints().isEmpty()) {
                        logEvent("plan_generate_success", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", schedule.getId(),
                                "violatedConstraintsCount", schedule.getViolatedConstraints().size(),
                                "result", "partial"
                        ));
                        return new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                    } else {
                        logEvent("plan_generate_success", "generate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", schedule.getId(),
                                "violatedConstraintsCount", schedule.getViolatedConstraints().size(),
                                "result", "accepted"
                        ));
                        return new ResponseEntity<>(HttpStatus.ACCEPTED);
                    }
                }
            }
            logEvent("plan_generate_failed", "generate", Map.of(
                    "durationMs", System.currentTimeMillis() - startTime,
                    "errorType", "BAD_REQUEST"
            ));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            MDC.remove(MODE_KEY);
        }
    }

    /*
     * This method is invoked by the frontend to request a regeneration of an existing shift schedule.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.POST, path = "regeneration/id={id}")
    public ResponseEntity<?> recreateSchedule(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        MDC.put(MODE_KEY, "regenerate");
        Map<String, Object> regenerateStartFields = new LinkedHashMap<>();
        regenerateStartFields.put("planId", id);
        logEvent("plan_regenerate_start", "regenerate", regenerateStartFields);
        try {
            if (id != null) {
                try {
                    if(schedulerController.recreateSchedule(id)) {
                        String newPlanId = MDC.get(NEW_PLAN_ID_KEY);
                        logEvent("plan_regenerate_success", "regenerate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "originalPlanId", id,
                                "newPlanId", newPlanId != null ? newPlanId : "unknown",
                                "result", "accepted"
                        ));
                        return new ResponseEntity<>(HttpStatus.ACCEPTED);
                    } else {
                        logEvent("plan_regenerate_failed", "regenerate", Map.of(
                                "durationMs", System.currentTimeMillis() - startTime,
                                "planId", id,
                                "errorType", "EXPECTATION_FAILED"
                        ));
                        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                    }
                } catch (UnableToBuildScheduleException e) {
                    logEvent("plan_regenerate_failed", "regenerate", Map.of(
                            "durationMs", System.currentTimeMillis() - startTime,
                            "planId", id,
                            "errorType", e.getClass().getSimpleName(),
                            "errorCode", "UNABLE_TO_BUILD_SCHEDULE"
                    ));
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            logEvent("plan_regenerate_failed", "regenerate", Map.of(
                    "durationMs", System.currentTimeMillis() - startTime,
                    "errorType", "BAD_REQUEST"
            ));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            MDC.remove(NEW_PLAN_ID_KEY);
            MDC.remove(MODE_KEY);
        }
    }

    /*
     * This method is invoked to retrieve all the existing shift schedules.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> readSchedules()  {

        List<ScheduleDTO> set = schedulerController.readSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }


    /**
     * Request send by the client when we want to show only the schedules to the planner
     * @return FOUND if the query had success, NOT FOUND if the query returned 0, ERROR if something went wrong
     */
    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER')")
    @RequestMapping(method = RequestMethod.GET,path = "/dates/")
    public ResponseEntity<?> getAllSchedulesWithDates()  {
        Set<ShowScheduleToPlannerDTO> showScheduleToPlannerDTOSet;
        try {
            showScheduleToPlannerDTOSet= schedulerController.getAllSchedulesWithDates();
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(schedulerController == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }else if(showScheduleToPlannerDTOSet.isEmpty()){
            return new ResponseEntity<>(showScheduleToPlannerDTOSet, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(showScheduleToPlannerDTOSet, HttpStatus.FOUND);

    }

    /*
     * This method is invoked to retrieve the illegal shift schedules.
     */
    @PreAuthorize("hasAnyRole('DOCTOR', 'PLANNER', 'CONFIGURATOR')")
    @RequestMapping(method = RequestMethod.GET,path = "illegals")
    public ResponseEntity<?> readIllegalSchedules()  {

        List<ScheduleDTO> set = schedulerController.readIllegalSchedules();
        return new ResponseEntity<>(set, HttpStatus.FOUND);

    }

    /*
     * This method is invoked to delete an existing shift schedule.
     */
    @PreAuthorize("hasAnyRole('PLANNER')")
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

    private void logEvent(String event, String mode, Map<String, Object> fields) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("event", event);
        data.put("requestId", getRequestId());
        data.put("mode", mode);
        data.putAll(fields);
        logger.info(formatLogMessage(data));
    }

    private String getRequestId() {
        String requestId = MDC.get("requestId");
        return requestId != null ? requestId : "unknown";
    }

    private String formatLogMessage(Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(entry.getKey()).append('=').append(formatValue(entry.getValue()));
        }
        return builder.toString();
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        String text = value.toString();
        if (text.contains(" ") || text.contains("=")) {
            return '"' + text.replace("\"", "\\\"") + '"';
        }
        return text;
    }


}
