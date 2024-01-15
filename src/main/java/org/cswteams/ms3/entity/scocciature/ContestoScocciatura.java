package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorUffaPriority;

@Data
public class ContestoScocciatura {

    private DoctorUffaPriority doctorUffaPriority;

    private ConcreteShift concreteShift;

    public ContestoScocciatura(DoctorUffaPriority utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.doctorUffaPriority = utente;
    }

}
