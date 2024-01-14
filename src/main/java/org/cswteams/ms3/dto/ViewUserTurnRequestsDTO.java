package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;
import java.util.Map;

@Data
@AllArgsConstructor
public class ViewUserTurnRequestsDTO {
    private long requestId;
    private Map<Locale, String> turnDescription;
    private long inizioEpoch;
    private long fineEpoch;
    private String userDetails; // name + lastname
    private Map<Locale, String> status;
}
