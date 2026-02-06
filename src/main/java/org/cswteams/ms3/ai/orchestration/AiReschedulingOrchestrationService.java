package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonFeedback;
import org.cswteams.ms3.control.toon.ToonPseudonymizationMapper;
import org.cswteams.ms3.control.toon.ToonPseudonymizationResult;
import org.cswteams.ms3.control.toon.ToonRequestContext;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorHolidays;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AiReschedulingOrchestrationService {
    private final ToonPseudonymizationMapper pseudonymizationMapper = new ToonPseudonymizationMapper();

    public AiReschedulingToonRequest buildToonRequestContext(LocalDate periodStart,
                                                             LocalDate periodEnd,
                                                             String mode,
                                                             List<ConcreteShift> concreteShifts,
                                                             List<Doctor> doctors,
                                                             List<DoctorUffaPriority> doctorUffaPriorities,
                                                             List<DoctorHolidays> doctorHolidays,
                                                             List<ToonActiveConstraint> activeConstraints,
                                                             List<ToonFeedback> feedbacks) {
        ToonPseudonymizationResult pseudonymized = pseudonymizationMapper.pseudonymize(
                doctors,
                doctorUffaPriorities,
                doctorHolidays,
                activeConstraints,
                feedbacks
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
}
