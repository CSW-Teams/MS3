package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorXY;

@Data
public class ContestoScocciatura {

    private DoctorXY doctorUffaPriority;

    private ConcreteShift concreteShift;

    public ContestoScocciatura(DoctorXY utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.doctorUffaPriority = utente;
    }

}
