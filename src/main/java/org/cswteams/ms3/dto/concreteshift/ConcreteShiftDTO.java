package org.cswteams.ms3.dto.concreteshift;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Getter;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Getter
public class ConcreteShiftDTO {

    private Long id;
    @Getter
    private Long idTurno;
    @Getter
    private long inizioEpoch;
    @Getter
    private long fineEpoch;

    private Set<UserDTO> utentiDiGuardia;
    private Set<UserDTO> utentiReperibili;

    @Getter
    private MedicalServiceDTO servizio;
    @Getter
    private TimeSlot timeSlot;

    private TaskEnum mansione;

    private boolean reperibilitaAttiva;

    private Set<UserDTO> retiredUsers;

    public ConcreteShiftDTO(Long id, long inizioEpoch, long fineEpoch, Set<UserDTO> utentiDiGuardia, Set<UserDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
    }

    public ConcreteShiftDTO(Long id, Long idTurno, long inizioEpoch, long fineEpoch, Set<UserDTO> utentiDiGuardia, Set<UserDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno, boolean reperibilitaAttiva) {
        this.id = id;
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
        this.idTurno=idTurno;
        this.reperibilitaAttiva = reperibilitaAttiva;

        this.retiredUsers = new HashSet<>();
    }

    protected ConcreteShiftDTO(){}

    public ConcreteShiftDTO(long inizioEpoch, long fineEpoch, Set<UserDTO> utentiDiGuardia, Set<UserDTO> utentiReperibili, MedicalServiceDTO servizio, TimeSlot turno) {
        this.inizioEpoch = inizioEpoch;
        this.fineEpoch = fineEpoch;
        this.utentiDiGuardia = utentiDiGuardia;
        this.utentiReperibili = utentiReperibili;
        this.timeSlot = turno;
        this.servizio = servizio;
    }

}
