package org.cswteams.ms3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.cswteams.ms3.enums.TaskEnum;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class AddConcreteShiftDTO {

    private LocalDate date;
    private TimeSlot timeSlot;
    private TaskEnum mansione;
    private Set<Long> utentiDiGuardia;
    private Set<Long> utentiReperibili;
    private String servizio;
    private boolean forced;
}
