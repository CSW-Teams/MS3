package org.cswteams.ms3.ai.broker.mapper;

import org.cswteams.ms3.ai.broker.domain.AiAssignment;
import org.cswteams.ms3.ai.broker.domain.AiMetadata;
import org.cswteams.ms3.ai.broker.domain.AiMetrics;
import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.broker.domain.AiStdDev;
import org.cswteams.ms3.ai.broker.domain.AiUffaBalance;
import org.cswteams.ms3.ai.broker.domain.AiUffaDelta;
import org.cswteams.ms3.ai.broker.domain.AiUncoveredShift;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetadataDto;
import org.cswteams.ms3.ai.protocol.dto.AiMetricsDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.dto.AiStdDevDto;
import org.cswteams.ms3.ai.protocol.dto.AiUffaBalanceDto;
import org.cswteams.ms3.ai.protocol.dto.AiUffaDeltaDto;
import org.cswteams.ms3.ai.protocol.dto.AiUncoveredShiftDto;

import java.util.List;
import java.util.stream.Collectors;

public class AiScheduleResponseMapper {

    public AiScheduleResponse toDomain(AiScheduleResponseDto dto) {
        if (dto == null) {
            return null;
        }
        return new AiScheduleResponse(
                dto.status,
                toMetadata(dto.metadata),
                mapAssignments(dto.assignments),
                mapUncovered(dto.uncoveredShifts),
                mapUffaDelta(dto.uffaDelta)
        );
    }

    private AiMetadata toMetadata(AiMetadataDto dto) {
        if (dto == null) {
            return null;
        }
        return new AiMetadata(dto.reasoning, dto.algorithm, dto.optimalityScore, toMetrics(dto.metrics));
    }

    private AiMetrics toMetrics(AiMetricsDto dto) {
        if (dto == null) {
            return null;
        }
        return new AiMetrics(dto.coveragePercent, toUffaBalance(dto.uffaBalance), dto.softViolationsCount);
    }

    private AiUffaBalance toUffaBalance(AiUffaBalanceDto dto) {
        if (dto == null) {
            return null;
        }
        return new AiUffaBalance(toStdDev(dto.nightShiftStdDev));
    }

    private AiStdDev toStdDev(AiStdDevDto dto) {
        if (dto == null) {
            return null;
        }
        return new AiStdDev(dto.initial, dto.finalValue);
    }

    private List<AiAssignment> mapAssignments(List<AiAssignmentDto> assignments) {
        if (assignments == null) {
            return List.of();
        }
        return assignments.stream()
                .map(item -> new AiAssignment(
                        item.shiftId,
                        item.doctorId,
                        item.roleCovered,
                        Boolean.TRUE.equals(item.isForced),
                        item.violationNote
                ))
                .collect(Collectors.toList());
    }

    private List<AiUncoveredShift> mapUncovered(List<AiUncoveredShiftDto> uncovered) {
        if (uncovered == null) {
            return List.of();
        }
        return uncovered.stream()
                .map(item -> new AiUncoveredShift(item.shiftId, item.reason))
                .collect(Collectors.toList());
    }

    private List<AiUffaDelta> mapUffaDelta(List<AiUffaDeltaDto> deltas) {
        if (deltas == null) {
            return List.of();
        }
        return deltas.stream()
                .map(item -> new AiUffaDelta(item.doctorId, item.queue, item.points))
                .collect(Collectors.toList());
    }
}
