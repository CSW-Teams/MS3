package org.cswteams.ms3.dto.shift;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
public class QuantityShiftSeniorityDTO {
    @Setter
    private long task;
    private String taskName;
    private final String seniority;
    private final int quantity;

    public QuantityShiftSeniorityDTO(long idTask, String seniory, int quantity) {
        this.task = idTask;
        this.seniority = seniory;
        this.quantity = quantity;
    }

    public QuantityShiftSeniorityDTO(
            @JsonProperty("taskName") String taskName,
            @JsonProperty("seniority") String seniority,
            @JsonProperty("quantity") int quantity) {
        this.taskName = taskName;
        this.seniority = seniority;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "MedicalServiceShiftDTO{" +
                "task=" + task +
                ", taskName='" + taskName +
                "', seniory='" + seniority +
                ", quantity=" + quantity +
                "}";
    }

}
