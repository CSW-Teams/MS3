package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.Getter;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.util.Set;

@Data
@AllArgsConstructor
public class ConcreteShiftDTO {

    private Long id;
    private Long idTurno;
    private long inizioEpoch;
    private long fineEpoch;
    private Set<UserDTO> utentiDiGuardia;
    private Set<UserDTO> utentiReperibili;
    private MedicalServiceDTO servizio;
    private TimeSlot timeSlot;
    private TaskEnum mansione;
    private boolean reperibilitaAttiva;
    private Set<UserDTO> retiredUsers;
}
