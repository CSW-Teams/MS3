package org.cswteams.ms3.control.scheduleFeedback;

import org.cswteams.ms3.dto.scheduleFeedback.ScheduleFeedbackDTO;

import java.util.List;

public interface IScheduleFeedbackController {
    ScheduleFeedbackDTO addFeedback(ScheduleFeedbackDTO feedbackDTO, String email);
    List<ScheduleFeedbackDTO> getAllFeedbacks();
    List<ScheduleFeedbackDTO> getFeedbacksByDoctor(String email);
    ScheduleFeedbackDTO updateFeedback(ScheduleFeedbackDTO feedbackDTO, String email);
    void deleteFeedback(Long feedbackId, String email);
}
