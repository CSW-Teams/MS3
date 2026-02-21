package org.cswteams.ms3.ai.protocol;

import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiRoleValidationScratchpadItemDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.exceptions.AiProtocolException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.cswteams.ms3.enums.Seniority;
import org.springframework.stereotype.Service;

@Service
public class AiScheduleSemanticValidator {

    public void validate(AiScheduleResponseDto dto) {
        List<ValidationError> errors = new ArrayList<>();
        if (dto == null) {
            errors.add(new ValidationError("$", "response must not be null"));
            throw AiProtocolException.schemaMismatch("AI response schema validation failed", errors, null);
        }

        if (dto.status == null) {
            errors.add(new ValidationError("$.status", "must not be null"));
        }
        if (dto.metadata == null) {
            errors.add(new ValidationError("$.metadata", "must not be null"));
        }
        if (dto.assignments == null) {
            errors.add(new ValidationError("$.assignments", "must not be null"));
        }

        validateMetadata(dto.metadata, errors);
        validateAssignments(dto.assignments, errors);

        if (!errors.isEmpty()) {
            throw AiProtocolException.schemaMismatch("AI response schema validation failed", errors, null);
        }
    }

    private void validateMetadata(AiMetadataDto metadata, List<ValidationError> errors) {
        if (metadata == null) {
            return;
        }
        if (!nonBlank(metadata.reasoning)) {
            errors.add(new ValidationError("$.metadata.reasoning", "must not be blank"));
        }
        if (metadata.optimalityScore == null) {
            errors.add(new ValidationError("$.metadata.optimality_score", "must not be null"));
        } else if (!inRange(metadata.optimalityScore, 0.0, 1.0)) {
            errors.add(new ValidationError("$.metadata.optimality_score", "must be between 0 and 1"));
        }
        if (metadata.metrics == null) {
            errors.add(new ValidationError("$.metadata.metrics", "must not be null"));
            return;
        }
        validateMetrics(metadata.metrics, errors);
        validateRoleValidationScratchpad(metadata.roleValidationScratchpad, errors);
    }

    private void validateRoleValidationScratchpad(List<AiRoleValidationScratchpadItemDto> scratchpad,
                                                  List<ValidationError> errors) {
        if (scratchpad == null) {
            return;
        }
        for (int i = 0; i < scratchpad.size(); i++) {
            AiRoleValidationScratchpadItemDto item = scratchpad.get(i);
            if (item == null) {
                errors.add(new ValidationError("$.metadata.role_validation_scratchpad[" + i + "]", "must not be null"));
                continue;
            }
            if (!nonBlank(item.shiftId)) {
                errors.add(new ValidationError("$.metadata.role_validation_scratchpad[" + i + "].shift_id", "must not be blank"));
            }
            if (!nonBlank(item.roleRequired)) {
                errors.add(new ValidationError("$.metadata.role_validation_scratchpad[" + i + "].role_required", "must not be blank"));
            } else if (!isAllowedRole(item.roleRequired)) {
                errors.add(new ValidationError("$.metadata.role_validation_scratchpad[" + i + "].role_required", "must be a valid Seniority enum name"));
            }
            if (item.candidateDoctorIds == null) {
                errors.add(new ValidationError("$.metadata.role_validation_scratchpad[" + i + "].candidate_doctor_ids", "must not be null"));
                continue;
            }
            Set<Integer> seenIds = new HashSet<>();
            for (int j = 0; j < item.candidateDoctorIds.size(); j++) {
                Integer candidateId = item.candidateDoctorIds.get(j);
                String path = "$.metadata.role_validation_scratchpad[" + i + "].candidate_doctor_ids[" + j + "]";
                if (!positiveInt(candidateId)) {
                    errors.add(new ValidationError(path, "must be > 0"));
                    continue;
                }
                if (!seenIds.add(candidateId)) {
                    errors.add(new ValidationError(path, "must not contain duplicates"));
                }
            }
        }
    }

    private void validateMetrics(AiMetricsDto metrics, List<ValidationError> errors) {
        if (metrics.coveragePercent == null) {
            errors.add(new ValidationError("$.metadata.metrics.coverage_percent", "must not be null"));
        } else if (!inRange(metrics.coveragePercent, 0.0, 100.0)) {
            errors.add(new ValidationError("$.metadata.metrics.coverage_percent", "must be between 0 and 1"));
        }
        if (metrics.softViolationsCount == null) {
            errors.add(new ValidationError("$.metadata.metrics.soft_violations_count", "must not be null"));
        } else if (metrics.softViolationsCount < 0) {
            errors.add(new ValidationError("$.metadata.metrics.soft_violations_count", "must be >= 0"));
        }
    }

    private void validateAssignments(List<AiAssignmentDto> assignments, List<ValidationError> errors) {
        if (assignments == null) {
            return;
        }
        Set<String> keys = new HashSet<>();
        for (int i = 0; i < assignments.size(); i++) {
            AiAssignmentDto assignment = assignments.get(i);
            if (assignment == null) {
                errors.add(new ValidationError("$.assignments[" + i + "]", "must not be null"));
                continue;
            }
            if (!nonBlank(assignment.shiftId)) {
                errors.add(new ValidationError("$.assignments[" + i + "].shift_id", "must not be blank"));
            } else if (!isValidShiftId(assignment.shiftId)) {
                errors.add(new ValidationError("$.assignments[" + i + "].shift_id", "must match format S_<id>_<yyyyMMdd>"));
            }
            if (!positiveInt(assignment.doctorId)) {
                errors.add(new ValidationError("$.assignments[" + i + "].doctor_id", "must be > 0"));
            }
            if (assignment.roleCovered == null) {
                errors.add(new ValidationError("$.assignments[" + i + "].role_covered", "must not be null"));
            } else if (assignment.roleCovered != Seniority.STRUCTURED
                    && assignment.roleCovered != Seniority.SPECIALIST_JUNIOR
                    && assignment.roleCovered != Seniority.SPECIALIST_SENIOR) {
                errors.add(new ValidationError("$.assignments[" + i + "].role_covered", "must be STRUCTURED or JUNIOR"));
            }
            if (assignment.isForced == null) {
                errors.add(new ValidationError("$.assignments[" + i + "].is_forced", "must not be null"));
            } else if (Boolean.TRUE.equals(assignment.isForced) && !nonBlank(assignment.violationNote)) {
                errors.add(new ValidationError("$.assignments[" + i + "].violation_note", "must not be blank when is_forced is true"));
            }

            if (nonBlank(assignment.shiftId) && assignment.doctorId != null) {
                String key = assignment.shiftId + "||" + assignment.doctorId;
                if (!keys.add(key)) {
                    errors.add(new ValidationError("$.assignments", "duplicate key shift_id=" + assignment.shiftId + " doctor_id=" + assignment.doctorId));
                }
            }
        }
    }

    private static boolean nonBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static boolean inRange(Double value, double min, double max) {
        return value != null && value >= min && value <= max;
    }

    private static boolean positiveInt(Integer value) {
        return value != null && value > 0;
    }

    private static boolean isAllowedRole(String roleRequired) {
        try {
            Seniority.valueOf(roleRequired);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean isValidShiftId(String shiftId) {
        if (!shiftId.matches("^S_[A-Za-z0-9]+_[0-9]{8}$")) {
            return false;
        }
        int lastUnderscore = shiftId.lastIndexOf('_');
        String datePart = shiftId.substring(lastUnderscore + 1);
        try {
            LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }
}
