package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoScocciatura {

    private UserScheduleState userScheduleState;

    private ConcreteShift concreteShift;

    public ContestoScocciatura(UserScheduleState utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.userScheduleState = utente;
    }

}
