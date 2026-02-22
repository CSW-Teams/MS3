package org.cswteams.ms3.entity;

import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * Incapsula tutte le informazioni sui livelli di priorità ("uffa") di un medico
 * per le diverse code di assegnazione (GENERAL, LONG_SHIFT, NIGHT).
 * Il sistema delle priorità UFFA mantiene, per ogni medico, tre code indipendenti.
 * Ogni coda ha un valore persistente e un valore "parziale" usato per ordinare i candidati.
 *
 * Fa parte della "Pipeline priorità (UFFA/scocciatura)" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
@Table(name = "doctor_uffa_priority")
@Getter
@Setter
/*
TODO: Check why there is this constraint
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames={
        "doctor_id",
        "schedule_id",
    })
})
*/
public class DoctorUffaPriority {
    
    @Id
    @GeneratedValue
    private Long id;

    /** Il {@link Doctor medico} a cui si riferisce la priorità Uffa. */
    @OneToOne
    @NotNull
    @JoinColumn(name = "doctor_ms3_tenant_user_id", referencedColumnName = "ms3_tenant_user_id")
    private Doctor doctor;

    /** Lo {@link Schedule schedule} corrente a cui sono associate le priorità. */
    @OneToOne
    @JoinColumn(name = "schedule_schedule_id", referencedColumnName = "schedule_id")
    private Schedule schedule;

    /** Priorità parziale per la coda {@link PriorityQueueEnum#GENERAL}. Utilizzata per l'ordinamento temporaneo durante la generazione. */
    @Column(name = "partial_general_priority")
    private int partialGeneralPriority = 0;

    /** Priorità persistente per la coda {@link PriorityQueueEnum#GENERAL}. */
    @Column(name = "general_priority")
    private int generalPriority = 0;

    /** Priorità parziale per la coda {@link PriorityQueueEnum#LONG_SHIFT}. Utilizzata per l'ordinamento temporaneo. */
    @Column(name = "partial_long_shift_priority")
    private int partialLongShiftPriority = 0;

    /** Priorità persistente per la coda {@link PriorityQueueEnum#LONG_SHIFT}. */
    @Column(name = "long_shift_priority")
    private int longShiftPriority = 0;

    /** Priorità parziale per la coda {@link PriorityQueueEnum#NIGHT}. Utilizzata per l'ordinamento temporaneo. */
    @Column(name = "partial_night_priority")
    private int partialNightPriority = 0;

    /** Priorità persistente per la coda {@link PriorityQueueEnum#NIGHT}. */
    @Column(name = "night_priority")
    private int nightPriority = 0;

    /** Tutti i {@link ConcreteShift turni concreti} assegnati al medico nello schedule corrente. Cache per ottimizzazione. */
    @Transient
    List<ConcreteShift> assegnazioniTurnoCache;


    /**
     * Default constructor needed by Lombok
     */
    public DoctorUffaPriority() {
    }

    public DoctorUffaPriority(Doctor doctor) {

        this.doctor = doctor;

    }

    public DoctorUffaPriority(Doctor doctor, Schedule schedule) {

        this.doctor = doctor;
        this.schedule = schedule;
    }


    /**
     * Restituisce una lista di {@link ConcreteShift turni concreti} assegnati al medico nello {@link Schedule schedule} corrente.
     * Questa lista viene inizializzata e memorizzata nella cache al primo accesso.
     * @return Lista di turni concreti assegnati al medico.
     */
    public List<ConcreteShift> getAssegnazioniTurnoCache(){

        if (assegnazioniTurnoCache == null){
            this.assegnazioniTurnoCache = new ArrayList<>();
            if (schedule == null || schedule.getConcreteShifts() == null) {
                return assegnazioniTurnoCache;
            }
            for (ConcreteShift concreteShift: schedule.getConcreteShifts()){
                for (DoctorAssignment da : concreteShift.getDoctorAssignmentList()) {
                    if (da.getDoctor().getId() == this.doctor.getId()){
                        assegnazioniTurnoCache.add(concreteShift);
                        break;
                    }
                }
            }
        }
        return assegnazioniTurnoCache;

    }

    /**
     * Aggiunge (in ordine cronologico) il nuovo {@link ConcreteShift turno concreto} alla lista
     * dei turni già assegnati al medico.
     * @param newConcreteShift Il nuovo turno concreto da aggiungere.
     */
    public void addConcreteShift(ConcreteShift newConcreteShift){
        List<ConcreteShift> concreteShiftList = getAssegnazioniTurnoCache();
        int idInsert = concreteShiftList.size();
        for(int i = 0; i < concreteShiftList.size(); i++){
            if(concreteShiftList.get(i).getDate() > newConcreteShift.getDate() || concreteShiftList.get(i).getDate() == newConcreteShift.getDate()){
                if(concreteShiftList.get(i).getShift().getStartTime().isAfter(newConcreteShift.getShift().getStartTime())) {
                    idInsert = i;
                }
            }
        }
        concreteShiftList.add(idInsert,newConcreteShift);
    }

    /**
     * Aggiorna il valore persistente della priorità ("uffa") per una specifica coda,
     * copiando il valore della priorità parziale.
     * Questo metodo è chiamato quando un medico viene scelto per un turno,
     * aggiornando i valori "persistenti" della coda (Microtask 1.2).
     *
     * @param pq La {@link PriorityQueueEnum coda di priorità} da aggiornare (GENERAL, LONG_SHIFT, NIGHT).
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
     */
    public void updatePriority(PriorityQueueEnum pq) {  //saveUffaTemp() counterpart

        switch(pq) {
            case GENERAL:
                this.generalPriority = this.partialGeneralPriority;
                break;

            case LONG_SHIFT:
                this.longShiftPriority = this.partialLongShiftPriority;
                break;

            case NIGHT:
                this.nightPriority = this.partialNightPriority;
                break;

        }

    }

    /**
     * Aggiorna il valore della priorità parziale per una specifica coda, applicando un delta
     * e assicurandosi che il nuovo valore rientri nei limiti superiore e inferiore.
     * Questo metodo è chiamato per calcolare un delta di "uffa" per ciascun medico,
     * aggiornando i valori "parziali" della coda interessata (Microtask 1.2).
     *
     * @param priorityDelta Il delta di priorità da applicare.
     * @param pq La {@link PriorityQueueEnum coda di priorità} da aggiornare (GENERAL, LONG_SHIFT, NIGHT).
     * @param upperBound Limite superiore per il valore della priorità.
     * @param lowerBound Limite inferiore per il valore della priorità.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
     */
    public void updatePartialPriority(int priorityDelta, PriorityQueueEnum pq, int upperBound, int lowerBound) {  //addUffaTemp() counterpart
        int newPartialPriority;

        switch(pq) {
            case GENERAL:
                newPartialPriority = Math.max(this.generalPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialGeneralPriority = newPartialPriority;
                break;

            case LONG_SHIFT:
                newPartialPriority = Math.max(this.longShiftPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialLongShiftPriority = newPartialPriority;
                break;

            case NIGHT:
                newPartialPriority = Math.max(this.nightPriority+priorityDelta, lowerBound);  //we ensure that new priority level stays into the bounds.
                newPartialPriority = Math.min(newPartialPriority, upperBound);
                this.partialNightPriority = newPartialPriority;
                break;

        }

    }

}
