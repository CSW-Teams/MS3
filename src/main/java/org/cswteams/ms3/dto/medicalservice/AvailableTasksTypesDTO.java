package org.cswteams.ms3.dto.medicalservice;

import lombok.Getter;
import org.cswteams.ms3.enums.TaskEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AvailableTasksTypesDTO {

    private final List<String> availableTaskTypes = new ArrayList<>();

    public AvailableTasksTypesDTO() {
        for (TaskEnum task : TaskEnum.values()) {
            availableTaskTypes.add(task.name());
        }
    }
}
