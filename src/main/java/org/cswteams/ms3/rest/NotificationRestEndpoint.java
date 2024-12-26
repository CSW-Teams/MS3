package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.notification.INotificationSystemController;
import org.cswteams.ms3.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RestController
@RequestMapping("/notification/")
@PreAuthorize("hasAnyRole('CONFIGURATOR', 'DOCTOR', 'PLANNER')")
public class NotificationRestEndpoint {

    @Autowired
    INotificationSystemController notificationSystemController;

    @PreAuthorize("hasAnyAuthority('configurator:get', 'doctor:get', 'planner:get')")
    @RequestMapping(method = RequestMethod.GET,path = "id={userId}")
    public ResponseEntity<?> getAllMedicalServices(@PathVariable long userId) {
        Set<NotificationDTO> notificationDTOS = notificationSystemController.getAllNotificationByUser(userId);
        if (notificationDTOS == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (notificationDTOS.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        return new ResponseEntity<>(notificationDTOS, HttpStatus.OK);
    }

    /*
    da fare il check su eventuali eccezioni sollevate
     */
    @PreAuthorize("hasAnyAuthority('configurator:put', 'doctor:put', 'planner:put')")
    @RequestMapping(method = RequestMethod.PUT, path = "updateStatus")
    public ResponseEntity<?> updateStatusNotification(@RequestBody @NotNull NotificationDTO notificationDTOS) {
        try {
            notificationSystemController.changeStatus(notificationDTOS);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
