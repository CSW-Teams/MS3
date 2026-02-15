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
        List<Constraint> constraints = constraintDAO.findAll();
        List<ToonActiveConstraint> mapped = new ArrayList<>();
        for (Constraint constraint : constraints) {
            ToonActiveConstraint activeConstraint = mapConstraint(constraint, doctors, concreteShifts);
            if (activeConstraint != null) {
                mapped.add(activeConstraint);
            }
        }
        return mapped;
    }

    private ToonActiveConstraint mapConstraint(Constraint constraint,
                                               List<Doctor> doctors,
                                               List<ConcreteShift> concreteShifts) {
        if (constraint == null) {
            logger.warn("event=ai_constraint_mapping_skipped reason=null_constraint");
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
}
