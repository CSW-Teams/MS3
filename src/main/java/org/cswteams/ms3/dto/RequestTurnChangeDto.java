package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestTurnChangeDto {
    private long concreteShiftId;
    private long senderId;
    private long receiverId;
}
