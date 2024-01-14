package org.cswteams.ms3.dto.concreteshift;

import lombok.Getter;
import org.cswteams.ms3.enums.Seniority;


/**
 * This DTO is used in the context of a substitution in a concrete shift. Its attributes are:
 * <ul>
 *     <li>the seniority of the requesting user, based on which we're able to determine the requested seniority of users who can replace the requesting one</li>
 *     <li>the concrete shift id</li>
 * </ul>
 */
@Getter
public class GetAvailableUsersForReplacementDTO {
    private final Seniority seniority;
    private final Long shiftId;

    public GetAvailableUsersForReplacementDTO(Seniority seniority, Long shiftId) {
        this.seniority = seniority;
        this.shiftId = shiftId;
    }
}
