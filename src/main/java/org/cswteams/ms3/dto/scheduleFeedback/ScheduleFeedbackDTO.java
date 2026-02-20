package org.cswteams.ms3.dto.scheduleFeedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleFeedbackDTO {
    private Long id;

    private Long doctorId;

    private String doctorName;

    private String doctorLastname;

    @NotEmpty(message = "Devi selezionare almeno un turno")
    private List<Long> concreteShiftIds;

    @Size(max = 255, message = "Il commento non può superare i 255 caratteri")
    private String comment;

    @Min(value = 1, message = "Il punteggio minimo è 1")
    @Max(value = 6, message = "Il punteggio massimo è 6")
    private int score;

    private long timestamp;
}
