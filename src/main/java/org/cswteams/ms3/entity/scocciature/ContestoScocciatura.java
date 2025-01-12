package org.cswteams.ms3.entity.scocciature;

import lombok.Data;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorUffaPriority;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "contesto_scocciatura")
public class ContestoScocciatura {

    @Column(name = "doctor_uffa_priority")
    private DoctorUffaPriority doctorUffaPriority;

    @Column(name = "concrete_shift")
    private ConcreteShift concreteShift;

    public ContestoScocciatura(DoctorUffaPriority utente, ConcreteShift turno){
        this.concreteShift = turno;
        this.doctorUffaPriority = utente;
    }

}
