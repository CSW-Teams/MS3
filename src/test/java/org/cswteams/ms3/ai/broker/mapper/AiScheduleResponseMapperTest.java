package org.cswteams.ms3.ai.broker.mapper;

import org.cswteams.ms3.ai.broker.domain.AiScheduleResponse;
import org.cswteams.ms3.ai.protocol.dto.AiAssignmentDto;
import org.cswteams.ms3.ai.protocol.dto.AiScheduleResponseDto;
import org.cswteams.ms3.ai.protocol.utils.AiStatus;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiScheduleResponseMapperTest {

    @Test
    void toDomainMapsAssignmentStatus() {
        AiAssignmentDto assignment = new AiAssignmentDto();
        assignment.shiftId = "S_1001_20260914";
        assignment.doctorId = 10;
        assignment.roleCovered = Seniority.STRUCTURED;
        assignment.assignmentStatus = ConcreteShiftDoctorStatus.ON_DUTY;
        assignment.isForced = Boolean.FALSE;
        assignment.violationNote = null;

        AiScheduleResponseDto dto = new AiScheduleResponseDto();
        dto.status = AiStatus.SUCCESS;
        dto.assignments = List.of(assignment);

        AiScheduleResponseMapper mapper = new AiScheduleResponseMapper();
        AiScheduleResponse response = mapper.toDomain(dto);

        assertEquals(ConcreteShiftDoctorStatus.ON_DUTY,
                response.getAssignments().get(0).getAssignmentStatus());
    }
}
