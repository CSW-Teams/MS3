package org.cswteams.ms3.entity.condition;

import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
public class TemporaryCondition extends Condition {

    @NotNull
    private long startDate;
    @NotNull
    private long endDate;

    public TemporaryCondition(String type,long startDate, long endDate) {
        super(type);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    protected TemporaryCondition() {

    }

    /**
     * Verifica se la categoria è valida in una dato giorno.
     * @param compareDate Giorno per cui si vuole verificare la validità della categoria
     * @return {@code true} se la categoria è valida in quel giorno, {@code false} altrimenti
     */
    public boolean isValid(long compareDate){
        return startDate >= 0 && endDate <= 0;
    }
}
