package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This DTO is different wrt <code>MedicalServiceDTO</code>: task types are here passed
 * by a list of <b>String</b> rather than a list of <code>Task</code>.
 * Strings will be properly adapted to <code>Task</code> object somewhere else.
 * <p>
 * It makes easier the creation of new medical services.
 */
@Getter
public class MedicalServiceCreationDTO {

    private String name;

    private List<String> taskTypes = new ArrayList<>();

    public MedicalServiceCreationDTO(String name, List<String> taskTypes) {
        this.name = name;
        this.taskTypes = taskTypes;
    }

    public MedicalServiceCreationDTO() {
    }
}
