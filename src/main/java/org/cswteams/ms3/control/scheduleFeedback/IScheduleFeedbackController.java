package org.cswteams.ms3.control.scheduleFeedback;

import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;

import java.util.List;

/**
 * Service-layer contract between REST and persistence orchestration for schedule feedback.
 * It receives endpoint-validated DTOs, enforces ownership/business checks, and returns planner-ready DTO projections.
 */
public interface IScheduleFeedbackController {
    ScheduleFeedbackDTO addFeedback(ScheduleFeedbackDTO feedbackDTO, String email);
    List<ScheduleFeedbackDTO> getAllFeedbacks();
    List<ScheduleFeedbackDTO> getFeedbacksByDoctor(String email);
    ScheduleFeedbackDTO updateFeedback(ScheduleFeedbackDTO feedbackDTO, String email);
    void deleteFeedback(Long feedbackId, String email);
}
