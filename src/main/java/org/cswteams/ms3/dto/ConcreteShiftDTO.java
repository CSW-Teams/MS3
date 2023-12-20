package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Getter;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
@AllArgsConstructor
public class ConcreteShiftDTO {

    @Getter
    private Long id;
    @Getter
    private Long idTurno;
    @Getter
    private long inizioEpoch;
    @Getter
    private long fineEpoch;

    private Set<DoctorDTO> utentiDiGuardia;
    private Set<DoctorDTO> utentiReperibili;

    @Getter
    private MedicalServiceDTO servizio;
    @Getter
    private TimeSlot timeSlot;

    private TaskEnum mansione;

    private boolean reperibilitaAttiva;

    private Set<DoctorDTO> retiredUsers;

    public ConcreteShiftDTO(Long id, long inizioEpoch, long fineEpoch, Set<DoctorDTO> utentiDiGuardia, Set<DoctorDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
    }

    public ConcreteShiftDTO(Long id, Long idTurno, long inizioEpoch, long fineEpoch, Set<DoctorDTO> utentiDiGuardia, Set<DoctorDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno, boolean reperibilitaAttiva) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
        this.idTurno=idTurno;
        this.reperibilitaAttiva = reperibilitaAttiva;
    }

    protected ConcreteShiftDTO(){}

    public ConcreteShiftDTO(long inizioEpoch, long fineEpoch, Set<DoctorDTO> utentiDiGuardia, Set<DoctorDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno) {
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
    }

}
