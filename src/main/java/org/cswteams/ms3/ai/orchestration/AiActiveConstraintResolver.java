package org.cswteams.ms3.ai.orchestration;

import org.cswteams.ms3.control.toon.ToonActiveConstraint;
import org.cswteams.ms3.control.toon.ToonBuilder;
import org.cswteams.ms3.control.toon.ToonConstraintEntityType;
import org.cswteams.ms3.control.toon.ToonConstraintType;
import org.cswteams.ms3.dao.ConstraintDAO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.constraint.Constraint;
import org.cswteams.ms3.entity.constraint.ConstraintAssegnazioneTurnoTurno;
import org.cswteams.ms3.entity.constraint.ConstraintHoliday;
import org.cswteams.ms3.entity.constraint.ConstraintMaxOrePeriodo;
import org.cswteams.ms3.entity.constraint.ConstraintMaxPeriodoConsecutivo;
import org.cswteams.ms3.entity.constraint.ConstraintNumeroDiRuoloTurno;
import org.cswteams.ms3.entity.constraint.ConstraintTurniContigui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Service
public class AiActiveConstraintResolver {
    private static final Logger logger = LoggerFactory.getLogger(AiActiveConstraintResolver.class);

    private final ConstraintDAO constraintDAO;

    @Autowired
    public AiActiveConstraintResolver(ConstraintDAO constraintDAO) {
        this.constraintDAO = constraintDAO;
    }

    public List<ToonActiveConstraint> resolve(List<Doctor> doctors, List<ConcreteShift> concreteShifts) {
        return resolveWithReport(doctors, concreteShifts, false).getResolvedConstraints();
    }

    public ResolveResult resolveWithReport(List<Doctor> doctors,
                                           List<ConcreteShift> concreteShifts,
                                           boolean failFastPolicy) {
        List<Constraint> constraints = constraintDAO.findAll();
        List<ToonActiveConstraint> mapped = new ArrayList<>();
        int skippedConstraints = 0;
        int hardConstraintsCount = 0;
        int softConstraintsCount = 0;
        for (Constraint constraint : constraints) {
            ToonActiveConstraint activeConstraint = mapConstraint(constraint, doctors, concreteShifts);
            if (activeConstraint != null) {
                mapped.add(activeConstraint);
                if (activeConstraint.getType() == ToonConstraintType.HARD) {
                    hardConstraintsCount++;
                } else if (activeConstraint.getType() == ToonConstraintType.SOFT) {
                    softConstraintsCount++;
                }
            } else {
                skippedConstraints++;
                if (failFastPolicy) {
                    throw new IllegalStateException("Fail-fast policy enabled: unusable active constraint detected.");
                }
            }
        }
        return new ResolveResult(mapped, skippedConstraints, hardConstraintsCount, softConstraintsCount);
    }

    private ToonActiveConstraint mapConstraint(Constraint constraint,
                                               List<Doctor> doctors,
                                               List<ConcreteShift> concreteShifts) {
        if (constraint == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=null_constraint constraint_id={}", (Object) null);
            return null;
        }

        ToonConstraintEntityType entityType = resolveEntityType(constraint);
        if (entityType == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=unsupported_constraint_type constraint_class={} constraint_id={}",
                    constraint.getClass().getSimpleName(),
                    constraint.getId());
            return null;
        }

        String entityId = resolveEntityId(entityType, doctors, concreteShifts);
        if (entityId == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=missing_entity_reference constraint_class={} constraint_id={} entity_type={}",
                    constraint.getClass().getSimpleName(),
                    constraint.getId(),
                    entityType);
            return null;
        }

        String reason = (constraint.getDescription() == null || constraint.getDescription().trim().isEmpty())
                ? constraint.getClass().getSimpleName()
                : constraint.getDescription().trim();

        ToonConstraintType type = constraint.isViolable() ? ToonConstraintType.SOFT : ToonConstraintType.HARD;
        if (type == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=missing_required_field constraint_id={} field=type", constraint.getId());
            return null;
        }
        if (entityType == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=missing_required_field constraint_id={} field=entityType", constraint.getId());
            return null;
        }
        if (entityId.trim().isEmpty()) {
            logger.warn("event=ai_constraint_mapping_skipped reason=missing_required_field constraint_id={} field=entityId", constraint.getId());
            return null;
        }
        if (reason.isEmpty()) {
            logger.warn("event=ai_constraint_mapping_skipped reason=missing_required_field constraint_id={} field=reason", constraint.getId());
            return null;
        }

        return new ToonActiveConstraint(type,
                entityType,
                entityId,
                reason,
                buildParams(constraint));
    }

    private ToonConstraintEntityType resolveEntityType(Constraint constraint) {
        if (constraint instanceof ConstraintNumeroDiRuoloTurno) {
            return ToonConstraintEntityType.SHIFT;
        }
        if (constraint instanceof ConstraintAssegnazioneTurnoTurno || constraint instanceof ConstraintHoliday) {
            return ToonConstraintEntityType.DOCTOR;
        }
        return null;
    }

    private String resolveEntityId(ToonConstraintEntityType entityType,
                                   List<Doctor> doctors,
                                   List<ConcreteShift> concreteShifts) {
        if (entityType == ToonConstraintEntityType.DOCTOR) {
            return doctors == null
                    ? null
                    : doctors.stream()
                    .filter(doctor -> doctor != null && doctor.getId() != null)
                                        .map(Doctor::getId)
                    .min(Long::compareTo)
                    .map(String::valueOf)
                    .orElse(null);
        }

        if (entityType == ToonConstraintEntityType.SHIFT) {
            if (concreteShifts == null) {
                return null;
            }
            return concreteShifts.stream()
                                        .filter(shift -> shift != null
                            && shift.getShift() != null
                            && shift.getShift().getTimeSlot() != null)
                    .sorted(Comparator
                            .comparingLong(ConcreteShift::getDate)
                            .thenComparing(shift -> shift.getShift().getTimeSlot().name())
                            .thenComparing(shift -> shift.getShift().getId() == null ? 0L : shift.getShift().getId()))
                    .map(ToonBuilder::shiftIdFor)
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    private Map<String, String> buildParams(Constraint constraint) {
        Map<String, String> params = new LinkedHashMap<>();
        if (constraint.getId() != null) {
            params.put("constraint_id", String.valueOf(constraint.getId()));
        }
        params.put("constraint_class", constraint.getClass().getSimpleName());

        if (constraint instanceof ConstraintMaxOrePeriodo) {
            ConstraintMaxOrePeriodo typed = (ConstraintMaxOrePeriodo) constraint;
            params.put("period_duration_days", String.valueOf(typed.getPeriodDuration()));
            params.put("period_max_time_minutes", String.valueOf(typed.getPeriodMaxTime()));
        } else if (constraint instanceof ConstraintMaxPeriodoConsecutivo) {
            ConstraintMaxPeriodoConsecutivo typed = (ConstraintMaxPeriodoConsecutivo) constraint;
            params.put("max_consecutive_minutes", String.valueOf(typed.getMaxConsecutiveMinutes()));
            if (typed.getConstrainedCategory() != null) {
                if (typed.getConstrainedCategory().getId() != null) {
                    params.put("constrained_category_id", String.valueOf(typed.getConstrainedCategory().getId()));
                }
                if (typed.getConstrainedCategory().getType() != null) {
                    params.put("constrained_category_type", typed.getConstrainedCategory().getType());
                }
            }
        } else if (constraint instanceof ConstraintTurniContigui) {
            ConstraintTurniContigui typed = (ConstraintTurniContigui) constraint;
            params.put("horizon", String.valueOf(typed.getHorizon()));
            if (typed.getTUnit() != null) {
                params.put("unit", typed.getTUnit().name());
            }
            if (typed.getTimeSlot() != null) {
                params.put("trigger_time_slot", typed.getTimeSlot().name());
            }
            Set<String> forbidden = new TreeSet<>();
            if (typed.getForbiddenTimeSlots() != null) {
                typed.getForbiddenTimeSlots().stream()
                        .filter(timeSlot -> timeSlot != null)
                        .map(Enum::name)
                        .forEach(forbidden::add);
            }
            if (!forbidden.isEmpty()) {
                params.put("forbidden_time_slots", String.join("|", forbidden));
            }
        }

        return params;
    }

    public static class ResolveResult {
        private final List<ToonActiveConstraint> resolvedConstraints;
        private final int skippedConstraints;
        private final int hardConstraintsCount;
        private final int softConstraintsCount;

        ResolveResult(List<ToonActiveConstraint> resolvedConstraints,
                      int skippedConstraints,
                      int hardConstraintsCount,
                      int softConstraintsCount) {
            this.resolvedConstraints = resolvedConstraints;
            this.skippedConstraints = skippedConstraints;
            this.hardConstraintsCount = hardConstraintsCount;
            this.softConstraintsCount = softConstraintsCount;
        }

        public List<ToonActiveConstraint> getResolvedConstraints() {
            return resolvedConstraints;
        }

        public int getSkippedConstraints() {
            return skippedConstraints;
        }

        public int getHardConstraintsCount() {
            return hardConstraintsCount;
        }

        public int getSoftConstraintsCount() {
            return softConstraintsCount;
        }
    }
}
