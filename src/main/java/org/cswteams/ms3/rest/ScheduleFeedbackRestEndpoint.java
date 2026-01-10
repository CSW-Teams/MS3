package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.scheduleFeedback.IScheduleFeedbackController;
import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/schedule-feedback")
public class ScheduleFeedbackRestEndpoint {

    @Autowired
    private IScheduleFeedbackController scheduleFeedbackController;

    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<ScheduleFeedbackDTO> addFeedback(@Valid @RequestBody ScheduleFeedbackDTO feedbackDTO) {
        if (feedbackDTO == null) {
            // JSON absent
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il corpo della richiesta non può essere vuoto");
        }

        String loggedUserEmail = getCurrentUserEmail();
        ScheduleFeedbackDTO result = scheduleFeedbackController.addFeedback(feedbackDTO, loggedUserEmail);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('PLANNER')")
    @GetMapping
    public ResponseEntity<List<ScheduleFeedbackDTO>> getAllFeedbacks() {
        List<ScheduleFeedbackDTO> feedbacks = scheduleFeedbackController.getAllFeedbacks();
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DOCTOR')")
    @GetMapping("/mine")
    public ResponseEntity<List<ScheduleFeedbackDTO>> getMyFeedbacks() {
        String loggedUserEmail = getCurrentUserEmail();

        List<ScheduleFeedbackDTO> feedbacks = scheduleFeedbackController.getFeedbacksByDoctor(loggedUserEmail);
        return new ResponseEntity<>(feedbacks, HttpStatus.OK);
    }

    // update endpoint non integrato. Valutarne integrazione futura con agenti AI?
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @PutMapping
    public ResponseEntity<ScheduleFeedbackDTO> updateFeedback(@Valid @RequestBody ScheduleFeedbackDTO feedbackDTO) {
        if (feedbackDTO == null || feedbackDTO.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID feedback mancante.");
        }
        String loggedUserEmail = getCurrentUserEmail();
        ScheduleFeedbackDTO updated = scheduleFeedbackController.updateFeedback(feedbackDTO, loggedUserEmail);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // delete endpoint non integrato. Valutarne integrazione futura con agenti AI?
    @PreAuthorize("hasAnyRole('DOCTOR')")
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long feedbackId) {
        String loggedUserEmail = getCurrentUserEmail();
        scheduleFeedbackController.deleteFeedback(feedbackId, loggedUserEmail);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }

        return null;
    }
}