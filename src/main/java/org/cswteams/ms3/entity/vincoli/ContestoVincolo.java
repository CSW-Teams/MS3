package org.cswteams.ms3.entity.vincoli;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.UserScheduleState;

@Data
public class ContestoVincolo {

    private UserScheduleState userScheduleState;

    private ConcreteShift concreteShift;

    public ContestoVincolo(UserScheduleState utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.userScheduleState = utente;
    }

}
