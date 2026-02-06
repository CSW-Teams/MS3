package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonPseudonymizationMapper;
import org.cswteams.ms3.control.toon.ToonPseudonymizationResult;
import org.cswteams.ms3.control.toon.ToonRequestContext;
import org.cswteams.ms3.dao.RequestRemovalFromConcreteShiftDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiReschedulingOrchestrationService {
    private static final String FEEDBACK_REASON_PENDING = "REMOVAL_PENDING";
    private static final String FEEDBACK_REASON_ACCEPTED = "REMOVAL_ACCEPTED";
    private static final String FEEDBACK_REASON_REJECTED = "REMOVAL_REJECTED";
    private static final int FEEDBACK_SEVERITY_PENDING = 6;
    private static final int FEEDBACK_SEVERITY_ACCEPTED = 8;
    private static final int FEEDBACK_SEVERITY_REJECTED = 4;

    private final ToonPseudonymizationMapper pseudonymizationMapper = new ToonPseudonymizationMapper();
    private final RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO;

    @Autowired
    public AiReschedulingOrchestrationService(RequestRemovalFromConcreteShiftDAO requestRemovalFromConcreteShiftDAO) {
        this.requestRemovalFromConcreteShiftDAO = requestRemovalFromConcreteShiftDAO;
    }

    public AiReschedulingToonRequest buildToonRequestContext(LocalDate periodStart,
                                                             LocalDate periodEnd,
                                                             String mode,
                                                             List<ConcreteShift> concreteShifts,
                                                             List<Doctor> doctors,
                                                             List<DoctorUffaPriority> doctorUffaPriorities,
                                                             List<DoctorHolidays> doctorHolidays,
                                                             List<ToonActiveConstraint> activeConstraints,
                                                             List<ToonFeedback> feedbacks) {
        List<ToonFeedback> resolvedFeedbacks = new ArrayList<>();
        if (feedbacks != null) {
            resolvedFeedbacks.addAll(feedbacks);
        }
        resolvedFeedbacks.addAll(loadScheduleFeedbacks(periodStart, periodEnd));
        ToonPseudonymizationResult pseudonymized = pseudonymizationMapper.pseudonymize(
                doctors,
                doctorUffaPriorities,
                doctorHolidays,
                activeConstraints,
                resolvedFeedbacks
        );

        ToonRequestContext context = new ToonRequestContext(
                periodStart,
                periodEnd,
                mode,
                concreteShifts,
                pseudonymized.getDoctors(),
                pseudonymized.getDoctorUffaPriorities(),
                pseudonymized.getDoctorHolidays(),
                pseudonymized.getActiveConstraints(),
                pseudonymized.getFeedbacks()
        );

        return new AiReschedulingToonRequest(context, pseudonymized.getPseudonymToDoctorId());
    }

    private List<ToonFeedback> loadScheduleFeedbacks(LocalDate periodStart, LocalDate periodEnd) {
        if (requestRemovalFromConcreteShiftDAO == null || periodStart == null || periodEnd == null) {
            return List.of();
        }
        long startEpoch = periodStart.toEpochDay();
        long endEpoch = periodEnd.toEpochDay();
        List<RequestRemovalFromConcreteShift> requests = requestRemovalFromConcreteShiftDAO
                .findAllByConcreteShiftDateBetween(startEpoch, endEpoch);
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        List<ToonFeedback> feedbacks = new ArrayList<>();
        for (RequestRemovalFromConcreteShift request : requests) {
            ToonFeedback feedback = toToonFeedback(request);
            if (feedback != null) {
                feedbacks.add(feedback);
            }
        }
        return feedbacks;
    }

    private ToonFeedback toToonFeedback(RequestRemovalFromConcreteShift request) {
        if (request == null || request.getConcreteShift() == null || request.getRequestingDoctor() == null) {
            return null;
        }
        String shiftId = ToonBuilder.shiftIdFor(request.getConcreteShift());
        return new ToonFeedback(
                shiftId,
                request.getRequestingDoctor().getId(),
                resolveReasonCode(request),
                resolveSeverity(request)
        );
    }

    private String resolveReasonCode(RequestRemovalFromConcreteShift request) {
        if (!request.isReviewed()) {
            return FEEDBACK_REASON_PENDING;
        }
        return request.isAccepted() ? FEEDBACK_REASON_ACCEPTED : FEEDBACK_REASON_REJECTED;
    }

    private int resolveSeverity(RequestRemovalFromConcreteShift request) {
        if (!request.isReviewed()) {
            return FEEDBACK_SEVERITY_PENDING;
        }
        return request.isAccepted() ? FEEDBACK_SEVERITY_ACCEPTED : FEEDBACK_SEVERITY_REJECTED;
    }
}
