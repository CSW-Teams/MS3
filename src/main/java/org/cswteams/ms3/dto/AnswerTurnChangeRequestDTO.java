package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AnswerTurnChangeRequestDTO {
    boolean hasAccepted;
    long requestID;
}
