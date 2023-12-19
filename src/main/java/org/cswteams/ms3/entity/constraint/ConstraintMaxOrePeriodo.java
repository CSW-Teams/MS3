package org.cswteams.ms3.entity.constraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloAssegnazioneTurnoTurnoException;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ConstraintMaxOrePeriodo extends ConstraintAssegnazioneTurnoTurno {
    @NotNull
    private int numGiorniPeriodo;

    @NotNull
    private long numMinutiMaxPeriodo;

    public ConstraintMaxOrePeriodo() {
    }

    public ConstraintMaxOrePeriodo(int numGiorniPeriodo, long numMinutiMax){
        this.numGiorniPeriodo = numGiorniPeriodo;
        this.numMinutiMaxPeriodo = numMinutiMax;
    }

    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {
                /*
        TODO: Refactor using EpochDAy format
        List<ConcreteShift> turniAssegnati = contesto.getDoctorScheduleState().getAssegnazioniTurnoCache();
        if(turniAssegnati != null && turniAssegnati.size() != 0) {
            // Trovo i limiti del periodo da considerare all'interno dello scheduling nel quale si trova il turno da assegnare
            LocalDate startPeriodDate = contesto.getDoctorScheduleState().getSchedule().getStartDate();
            LocalDate endPeriodDate = startPeriodDate.plusDays(numGiorniPeriodo);
            while(endPeriodDate.isBefore(contesto.getDoctorScheduleState().getSchedule().getEndDate())){
                if(contesto.getConcreteShift().getData().isBefore(endPeriodDate) && (contesto.getConcreteShift().getData().isAfter(startPeriodDate) || contesto.getConcreteShift().getData().isEqual(startPeriodDate))){
                    break;
                }
                startPeriodDate = endPeriodDate;
                endPeriodDate = endPeriodDate.plusDays(numGiorniPeriodo);
            }
            // Conto i minuti dei turni assegnati all'utente nel periodo considerato + il turno da assegnare
            long minutiComplessivi = contesto.getConcreteShift().getShift().getMinutidiLavoro();
            for(ConcreteShift at: turniAssegnati){
                if(at.getData().isBefore(endPeriodDate) && (at.getData().isAfter(startPeriodDate) || at.getData().isEqual(startPeriodDate))){
                    minutiComplessivi += at.getShift().getMinutidiLavoro();
                    if(minutiComplessivi > numMinutiMaxPeriodo){
                        throw new ViolatedVincoloAssegnazioneTurnoTurnoException(contesto.getConcreteShift(), contesto.getDoctorScheduleState().getDoctor(), numGiorniPeriodo, numMinutiMaxPeriodo);
                    }
                }
            }

        }
        */


    }
}
