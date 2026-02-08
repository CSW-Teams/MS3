package org.cswteams.ms3.entity.constraint;

import org.cswteams.ms3.control.utils.DoctorAssignmentUtil;
import org.cswteams.ms3.control.utils.ShiftUtil;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.Seniority;
import org.cswteams.ms3.exception.ViolatedConstraintException;
import org.cswteams.ms3.exception.ViolatedVincoloRuoloNumeroException;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementa il vincolo {@code ConstraintNumeroDiRuoloTurno}, che garantisce il rispetto
 * dei numeri minimi di medici per {@link Seniority seniority} (es. specialista, strutturato)
 * all'interno di un {@link ConcreteShift turno concreto}, sia per i medici "on duty" che per quelli "on call".
 *
 * Fa parte del "Catalogo vincoli attivi" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
@Entity
public class ConstraintNumeroDiRuoloTurno extends Constraint {
    /**
     * Verifica se il vincolo {@code ConstraintNumeroDiRuoloTurno} è rispettato quando si tenta di assegnare
     * un nuovo {@link ConcreteShift turno concreto} a un medico. Questo vincolo assicura che il turno
     * abbia il numero richiesto di medici per ogni {@link Seniority seniority}, sia per i medici
     * {@code ON_DUTY} che {@code ON_CALL}.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @throws ViolatedConstraintException Eccezione lanciata se il vincolo è violato (es. numero insufficiente
     *                                     di medici di una data seniority).
     */
    @Override
    public void verifyConstraint(ContextConstraint context) throws ViolatedConstraintException {

        //Verifico se sono stati allocati già tutti gli utenti necessari in guardia
        if (DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY)).size() != ShiftUtil.getNumRequiredDoctors(context.getConcreteShift().getShift())) {
            //Verifico se è possibile aggiungere l'utente in guardia
            verify(context, DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY)));
        }

        //Verifico se sono stati allocati già tutti gli utenti necessari in reperibilità
        else if(DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_CALL)).size() != ShiftUtil.getNumRequiredDoctors(context.getConcreteShift().getShift())){
            //Verifico se è possibile aggiungere l'utente in reperibilità
            verify(context, DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_CALL)));
        }

        //Entro in questo ramo quando devo aggiungere una nuova assegnazione manualmente
        else{

            //Verifico se in guardia e in reperibilità ho un numero di strutturati e specializzandi sufficienti
            this.countSeniority(context, DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_DUTY)));
            this.countSeniority(context, DoctorAssignmentUtil.getDoctorsInConcreteShift(context.getConcreteShift(), Collections.singletonList(ConcreteShiftDoctorStatus.ON_CALL)));

        }

    }

    /**
     * Metodo ausiliario utilizzato per verificare il vincolo {@code ConstraintNumeroDiRuoloTurno},
     * riutilizzando la logica sia per i medici {@code ON_DUTY} che {@code ON_CALL}.
     * Calcola il numero di medici già assegnati con la stessa {@link Seniority seniority} del medico
     * che si tenta di aggiungere e verifica se il numero richiesto di medici per quella seniority
     * è già stato raggiunto per il {@link ConcreteShift turno concreto}.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @param assignedDoctors Lista di medici ({@code ON_DUTY} o {@code ON_CALL}) già assegnati al turno.
     * @throws ViolatedVincoloRuoloNumeroException Eccezione lanciata se il vincolo è violato.
     */
    private void verify(ContextConstraint context, List<Doctor> assignedDoctors) throws ViolatedVincoloRuoloNumeroException {

        //We calculate the number of doctors already assigned with the same seniority of the doctor we want to add to the schedule.
        int numAssignedDoctorsForSeniority = 0;
        for (Doctor doctor : assignedDoctors) {
            if (doctor.getSeniority().equals(context.getDoctorUffaPriority().getDoctor().getSeniority()))
                numAssignedDoctorsForSeniority++;
        }
        //Loop on the seniorities
        for (QuantityShiftSeniority quantityShiftSeniority : context.getConcreteShift().getShift().getQuantityShiftSeniority()) {
            //If the required number of doctors with that seniority was already reached, then we raise an exception.
            //Otherwise, we can add the doctor to the concrete shift.
            if (quantityShiftSeniority.getSeniorityMap().containsKey(context.getDoctorUffaPriority().getDoctor().getSeniority())) {
                if (numAssignedDoctorsForSeniority >= quantityShiftSeniority.getSeniorityMap().get(context.getDoctorUffaPriority().getDoctor().getSeniority()))
                    throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), context.getDoctorUffaPriority().getDoctor());
            }
        }
    }

    /**
     * Metodo ausiliario che conta, in un {@link ConcreteShift turno concreto}, quanti medici per
     * ogni {@link Seniority seniority} sono {@code ON_DUTY} o {@code ON_CALL}.
     * Verifica se il numero di medici di ogni seniority nel turno è sufficiente rispetto ai requisiti.
     *
     * @param context Oggetto {@link ContextConstraint} che comprende il nuovo turno concreto da assegnare
     *                e le informazioni sullo stato del medico nello schedule corrispondente.
     * @param assignedDoctors Lista di medici ({@code ON_DUTY} o {@code ON_CALL}) già assegnati al turno.
     * @throws ViolatedVincoloRuoloNumeroException Eccezione lanciata se il vincolo è violato
     *                                             (es. numero insufficiente di medici di una data seniority).
     */
    private void countSeniority(ContextConstraint context, List<Doctor> assignedDoctors) throws ViolatedVincoloRuoloNumeroException {
        //We count how many doctors foreach seniority there are in the concrete shift.
        //The hashmap contains the information "seniority - number of doctors of that seniority in the concrete shift".
        HashMap<Seniority,Integer> counter = new HashMap<>();
        for (Doctor doctor : assignedDoctors) {
            Seniority seniority = doctor.getSeniority();
            counter.merge(seniority, 1, Integer::sum);
            /* IT IS EQUIVALENT TO:
            if(counter.get(seniority)==null){
                counter.put(seniority,1);
            }
            else{
                counter.put(seniority,counter.get(seniority)+1);
            }*/
        }

        //calcolo la mappa senioriti integer  per il turnno
        Map<Seniority, Integer> mapTotalNeed =new HashMap<>();
        for (QuantityShiftSeniority quantityShiftSeniority : context.getConcreteShift().getShift().getQuantityShiftSeniority()) {
            for(Map.Entry<Seniority,Integer> entry:quantityShiftSeniority.getSeniorityMap().entrySet()){
                mapTotalNeed.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
        }
        for(Map.Entry<Seniority,Integer> entry:mapTotalNeed.entrySet()){
            //We check if in the concrete shift we have the correct number of doctors foreach seniority.
            if (counter.get(entry.getKey()) != null && counter.get(entry.getKey()) < entry.getValue())
                throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), entry, counter.get(entry.getKey()));
            if (counter.get(entry.getKey()) == null)
                throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), entry, 0);
        }
    }

}
