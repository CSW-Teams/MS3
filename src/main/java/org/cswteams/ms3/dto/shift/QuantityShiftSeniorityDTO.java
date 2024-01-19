package org.cswteams.ms3.dto.shift;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.cswteams.ms3.entity.QuantityShiftSeniority;

import javax.persistence.Entity;

@Getter
public class QuantityShiftSeniorityDTO {
    private long task; //questo é da rivedere
    private String seniority;
    private int quantity;
    public QuantityShiftSeniorityDTO(long idTask,String seniory,int quantity ){
        this.task=idTask;
        this.seniority=seniory;
        this.quantity=quantity;
    }

}
