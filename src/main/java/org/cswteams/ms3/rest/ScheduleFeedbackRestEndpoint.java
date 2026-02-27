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

/**
 * REST boundary for post-baseline schedule feedback.
 * <p>
 * Who triggers this: doctors submit, update, and delete their own feedback after seeing a schedule baseline;
 * planners consume read endpoints to monitor feedback trends.
 * What response means: POST returns the created persisted projection, GET returns persisted projections,
 * PUT returns the latest persisted projection, DELETE returns 204 when the persisted record is removed.
 */
@RestController
@RequestMapping("/schedule-feedback")
public class ScheduleFeedbackRestEndpoint {

    @Autowired
    private IScheduleFeedbackController scheduleFeedbackController;

    /**
     * Request payload shape at endpoint boundary:
     * - JSON body deserializable to {@link ScheduleFeedbackDTO}
     * - concreteShiftIds is expected non-empty
     * - score is expected in range [1,6]
     * - comment is optional but <= 255 characters
     *
     * Validation assumption: doctor identity is always derived from authenticated principal,
     * never trusted from request body doctor fields.
     */
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

    /**
     * Planner-facing read model: returns all persisted schedule feedback entries with doctor identity and shift ids.
     */
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

    /**
     * Update semantics: only owner doctor can modify own feedback; expected mutable fields are comment and score.
     * Planner-facing behavior: planners subsequently read the updated version from GET /schedule-feedback.
     */
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

    /**
     * Delete semantics: hard delete by feedback id, only for owner doctor.
     * Planner-facing behavior: deleted feedback disappears from planner aggregate reads.
     */
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
