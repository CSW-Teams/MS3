package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewUserTurnRequestsDTO {
    private long requestId;
    private String turnDescription;
    private long inizioEpoch;
    private long fineEpoch;
    private String userDetails;
}
