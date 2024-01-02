package org.cswteams.ms3.dto;

import lombok.Data;
import lombok.Getter;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.entity.Task;
import org.cswteams.ms3.enums.TimeSlot;

import java.time.Duration;
import java.time.LocalTime;

@Data
public class TurnoDTO {

    @Getter
    private TimeSlot timeSlot;

    @Getter
    private LocalTime oraInizio;

    private Duration durata;

    @Getter
    private MedicalServiceDTO servizio;

    private Task task;

    //@Getter
    //private Set<Categoria> categorieVietate;

    private long id;

    private boolean reperibilitaAttiva;

    //private List<RuoloNumero> ruoliNumero;

    public TurnoDTO(){}

    public TurnoDTO(long id, TimeSlot timeSlot, LocalTime inizio, Duration durata, MedicalServiceDTO servizio, Task task, boolean reperibilitaAttiva){
        this.durata = durata;
        this.oraInizio = inizio;
        this.servizio = servizio;
        this.task = task;
        this.timeSlot = timeSlot;
        this.id = id;
        this.reperibilitaAttiva = reperibilitaAttiva;
        //this.ruoliNumero = ruoliNumero;
    }
}
