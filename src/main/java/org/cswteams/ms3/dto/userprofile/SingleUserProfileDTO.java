package org.cswteams.ms3.dto.userprofile;

import lombok.Getter;
import org.cswteams.ms3.dto.condition.PermanentConditionDTO;

import java.util.List;

// DTO from client to server in response of GET API call /api/users/user-profile/user_id={}
@Getter
public class SingleUserProfileDTO {
    private final Long id;
    private final String name;
    private final String lastname;
    private final String email;
    private final String birthday;
    private final String seniority;
    private final List<String> specializations;
    private final List<String> systemActors;
    private final List<PermanentConditionDTO> permanentConditions;
    private final List<TemporaryConditionDTO> temporaryConditions;

    /**
     * DTO that has the responsibility to hold the information needed to be shown in the user profile view on the frontend
     *
     * @param id                  ID of the user we want to show
     * @param name                Name of the user we want to show
     * @param lastname            Lastname of the user we want to show
     * @param email               Email of the user we want to show
     * @param birthday            Birthday of the user we want to show
     * @param seniority           Seniority of the user we want to show (STRUCTURED/SPECIALIZAT_JUNIOR/SPECIALIZAT_SENIOR)
     * @param specializations     Specializations of the user we want to show (E.g. CARDIOLOGY, ONCOLOGY, ecc..)
     * @param systemActors        Roles of the user in the MS3 system we want to show (PLANNER/DOCTOR/CONFIGURATOR)
     * @param permanentConditions List of permanent conditions held by the doctor
     * @param temporaryConditions List of temporary conditions held by the doctor
     */
    public SingleUserProfileDTO(Long id,
                                String name,
                                String lastname,
                                String email,
                                String birthday,
                                String seniority,
                                List<String> specializations,
                                List<String> systemActors, List<PermanentConditionDTO> permanentConditions, List<TemporaryConditionDTO> temporaryConditions) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.birthday = birthday;
        this.seniority = seniority;
        this.specializations = specializations;
        this.systemActors = systemActors;
        this.permanentConditions = permanentConditions;
        this.temporaryConditions = temporaryConditions;
    }
}
