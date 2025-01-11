package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;
import org.cswteams.ms3.dto.shift.ShiftDTOIn;
import org.cswteams.ms3.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * This DTO is different wrt {@link MedicalServiceDTO}: task types are here passed
 * by a list of <b>String</b> rather than a list of {@link Task}.
 * Strings will be properly adapted to {@link Task} object somewhere else.
 *
 * <p>
 * It makes easier the creation of new medical services.
 */
@Getter
public class MedicalServiceCreationDTO {

    private String name;

    private List<String> taskTypes = new ArrayList<>();

    private List<ShiftDTOIn> shifts = new ArrayList<>();

    public MedicalServiceCreationDTO(String name, List<String> taskTypes, List<ShiftDTOIn> shifts) {
        this.name = name;
        this.taskTypes = taskTypes;
        this.shifts = shifts;
    }

    public MedicalServiceCreationDTO() {
    }
}
