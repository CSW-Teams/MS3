package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonPseudonymizationMapper;
import org.cswteams.ms3.control.toon.ToonPseudonymizationResult;
import org.cswteams.ms3.control.toon.ToonRequestContext;
import org.cswteams.ms3.dao.ScheduleFeedbackDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.ScheduleFeedback;
import org.cswteams.ms3.entity.enums.FeedbackCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiReschedulingOrchestrationService {
    private final ToonPseudonymizationMapper pseudonymizationMapper = new ToonPseudonymizationMapper();
    private final ScheduleFeedbackDAO scheduleFeedbackDAO;
    private final AiActiveConstraintResolver aiActiveConstraintResolver;

    @Autowired
    public AiReschedulingOrchestrationService(ScheduleFeedbackDAO scheduleFeedbackDAO,
                                              AiActiveConstraintResolver aiActiveConstraintResolver) {
        this.scheduleFeedbackDAO = scheduleFeedbackDAO;
        this.aiActiveConstraintResolver = aiActiveConstraintResolver;
    }

    public List<ToonActiveConstraint> resolveActiveConstraints(List<Doctor> doctors,
                                                               List<ConcreteShift> concreteShifts) {
        if (aiActiveConstraintResolver == null) {
            return List.of();
        }
        AiActiveConstraintResolver.ResolveResult resolveResult = aiActiveConstraintResolver
                .resolveWithReport(doctors, concreteShifts, false);
        if (resolveResult == null) {
            return List.of();
        }
        List<ToonActiveConstraint> resolvedConstraints = resolveResult.getResolvedConstraints();
        return resolvedConstraints == null ? List.of() : resolvedConstraints;
    }

    public AiActiveConstraintResolver.ResolveResult resolveActiveConstraintsWithReport(List<Doctor> doctors,
                                                                                       List<ConcreteShift> concreteShifts,
                                                                                       boolean failFastPolicy) {
        if (aiActiveConstraintResolver == null) {
            return new AiActiveConstraintResolver.ResolveResult(List.of(), 0, 0, 0);
        }
        AiActiveConstraintResolver.ResolveResult resolveResult = aiActiveConstraintResolver
                .resolveWithReport(doctors, concreteShifts, failFastPolicy);
        return resolveResult == null
                ? new AiActiveConstraintResolver.ResolveResult(List.of(), 0, 0, 0)
                : resolveResult;
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
        List<ToonActiveConstraint> resolvedConstraints = activeConstraints;
        if (resolvedConstraints == null || resolvedConstraints.isEmpty()) {
            resolvedConstraints = resolveActiveConstraints(doctors, concreteShifts);
        }
        List<ToonFeedback> resolvedFeedbacks = new ArrayList<>();
        if (feedbacks != null) {
            resolvedFeedbacks.addAll(feedbacks);
        }
        resolvedFeedbacks.addAll(loadToonFeedbacksFromScheduleFeedback(periodStart, periodEnd));
        List<Doctor> scopedDoctors = doctors == null ? List.of() : doctors;
        Set<Long> scopedDoctorIds = new HashSet<>();
        for (Doctor doctor : scopedDoctors) {
            if (doctor != null && doctor.getId() != null) {
                scopedDoctorIds.add(doctor.getId());
            }
        }
        List<DoctorUffaPriority> scopedPriorities = new ArrayList<>();
        if (doctorUffaPriorities != null) {
            for (DoctorUffaPriority priority : doctorUffaPriorities) {
                if (priority != null && priority.getDoctor() != null && scopedDoctorIds.contains(priority.getDoctor().getId())) {
                    scopedPriorities.add(priority);
                }
            }
        }
        List<DoctorHolidays> scopedHolidays = new ArrayList<>();
        if (doctorHolidays != null) {
            for (DoctorHolidays holidays : doctorHolidays) {
                if (holidays != null && holidays.getDoctor() != null && scopedDoctorIds.contains(holidays.getDoctor().getId())) {
                    scopedHolidays.add(holidays);
                }
            }
        }

        ToonPseudonymizationResult pseudonymized = pseudonymizationMapper.pseudonymize(
                scopedDoctors,
                scopedPriorities,
                scopedHolidays,
                resolvedConstraints,
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

    private List<ToonFeedback> loadToonFeedbacksFromScheduleFeedback(LocalDate periodStart, LocalDate periodEnd) {
        if (scheduleFeedbackDAO == null || periodStart == null || periodEnd == null) {
            return List.of();
        }
        long startEpoch = periodStart.toEpochDay();
        long endEpoch = periodEnd.toEpochDay();
        List<ScheduleFeedback> scheduleFeedbacks = scheduleFeedbackDAO
                .findAllByConcreteShiftDateBetween(startEpoch, endEpoch);
        if (scheduleFeedbacks == null || scheduleFeedbacks.isEmpty()) {
            return List.of();
        }
        List<ToonFeedback> feedbacks = new ArrayList<>();
        for (ScheduleFeedback scheduleFeedback : scheduleFeedbacks) {
            feedbacks.addAll(toToonFeedback(scheduleFeedback));
        }
        return feedbacks;
    }

    private List<ToonFeedback> toToonFeedback(ScheduleFeedback scheduleFeedback) {
        if (scheduleFeedback == null || scheduleFeedback.getDoctor() == null || scheduleFeedback.getConcreteShifts() == null) {
            return List.of();
        }
        List<ToonFeedback> toonFeedbacks = new ArrayList<>();
        for (ConcreteShift concreteShift : scheduleFeedback.getConcreteShifts()) {
            if (concreteShift == null) {
                continue;
            }
            toonFeedbacks.add(new ToonFeedback(
                    ToonBuilder.shiftIdFor(concreteShift),
                    scheduleFeedback.getDoctor().getId(),
                    mapFeedbackCategoryToToonReasonCode(scheduleFeedback.getCategory()),
                    scheduleFeedback.getScore(),
                    scheduleFeedback.getComment()
            ));
        }
        return toonFeedbacks;
    }

    private String mapFeedbackCategoryToToonReasonCode(FeedbackCategory category) {
        if (category == null) {
            return FeedbackCategory.OTHER.name();
        }
        switch (category) {
            case REPEATED_WEEKDAY:
                return FeedbackCategory.REPEATED_WEEKDAY.name();
            case REPEATED_TIME_SLOT:
                return FeedbackCategory.REPEATED_TIME_SLOT.name();
            case CONSECUTIVE_SHIFTS:
                return FeedbackCategory.CONSECUTIVE_SHIFTS.name();
            case WORKLOAD_IMBALANCE:
                return FeedbackCategory.WORKLOAD_IMBALANCE.name();
            case PREFERENCE_VIOLATION:
                return FeedbackCategory.PREFERENCE_VIOLATION.name();
            case OTHER:
            default:
                return FeedbackCategory.OTHER.name();
        }
    }
}
