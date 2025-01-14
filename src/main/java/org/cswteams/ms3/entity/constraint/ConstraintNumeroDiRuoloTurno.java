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
 * This class implements the check of the number of doctors of each seniority in a concrete shift.
 * It defines how many doctors foreach seniority have to be associated to each concrete shift.
 * For instance, nocturne shifts in ward must have one specialist and one structured on duty and
 * one specialist and one structured on call.
 */
@Entity
public class ConstraintNumeroDiRuoloTurno extends Constraint {

    /**
     * This method checks if numeroDiRuoloTurno constraint is respected while inserting a new concrete shift into a schedule.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @throws ViolatedConstraintException Exception thrown if the constraint is violated
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
     * This auxiliary method is used to check the constraint and reuse the code. It makes the same checks both for on duty doctors
     * and for on call doctors.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @param assignedDoctors On duty doctors or on call doctors that have been already assigned
     * @throws ViolatedVincoloRuoloNumeroException Exception thrown if the constraint is violated
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
     * This auxiliary method counts, in a concrete shift, how many doctors foreach seniority are on duty or on call.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @param assignedDoctors On duty doctors or on call doctors that have been already assigned
     * @throws ViolatedVincoloRuoloNumeroException Exception thrown if the constraint is violated
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
