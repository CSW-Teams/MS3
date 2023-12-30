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
import java.security.KeyStore;
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
    public void verificaVincolo(ContestoVincolo context) throws ViolatedConstraintException {

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
    private void verify(ContestoVincolo context, List<Doctor> assignedDoctors) throws ViolatedVincoloRuoloNumeroException {

        //We calculate the number of doctors already assigned with the same seniority of the doctor we want to add to the schedule.
        int numAssignedDoctorsForSeniority = 0;
        for (Doctor doctor : assignedDoctors) {
            if (doctor.getSeniority().equals(context.getDoctorScheduleState().getDoctor().getSeniority()))
                numAssignedDoctorsForSeniority++;
        }

        //Loop on the seniorities
        for (Map.Entry<Seniority, Integer> quantityShiftSeniority : context.getConcreteShift().getShift().getQuantityShiftSeniority().entrySet()) {
            //If the required number of doctors with that seniority was already reached, then we raise an exception.
            //Otherwise, we can add the doctor to the concrete shift.
            if (quantityShiftSeniority.getKey().equals(context.getDoctorScheduleState().getDoctor().getSeniority())) {
                if (numAssignedDoctorsForSeniority >= quantityShiftSeniority.getValue())
                    throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), context.getDoctorScheduleState().getDoctor());
            }
        }

    }

    /**
     * This auxiliary method counts, in a concrete shift, how many doctors foreach seniority are on duty or on call.
     * @param context Object comprehending the new concrete shift to be assigned and the information about doctor's state in the corresponding schedule
     * @param assignedDoctors On duty doctors or on call doctors that have been already assigned
     * @throws ViolatedVincoloRuoloNumeroException Exception thrown if the constraint is violated
     */
    private void countSeniority(ContestoVincolo context, List<Doctor> assignedDoctors) throws ViolatedVincoloRuoloNumeroException {
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
            }
             */

        }

        //We check if in the concrete shift we have the correct number of doctors foreach seniority.
        for (Map.Entry<Seniority, Integer>  quantityShiftSeniority : context.getConcreteShift().getShift().getQuantityShiftSeniority().entrySet()) {
            if (counter.get(quantityShiftSeniority.getKey()) != null && counter.get(quantityShiftSeniority.getKey()) < quantityShiftSeniority.getValue())
                throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), quantityShiftSeniority, counter.get(quantityShiftSeniority.getKey()));
            if (counter.get(quantityShiftSeniority.getKey()) == null)
                throw new ViolatedVincoloRuoloNumeroException(context.getConcreteShift(), quantityShiftSeniority, 0);
        }

    }

}
