package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.scheduling.algo.DoctorUffaPriority;
import org.cswteams.ms3.entity.scocciature.ContestoScocciatura;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.*;

/**
 * This class manages all the aspects concerning the uffa prioriy levels.
 */
public class ControllerScocciaturaPriority {

    public List<Scocciatura> scocciature;   //why public!?
    private final int upperBound;
    private final int lowerBound;


    public ControllerScocciaturaPriority(List<Scocciatura> scocciature) {
        this.scocciature = scocciature;

        //we read upper bound and lower bound of priority levels from configuration file priority.properties
        try {
            File file = new File("src/main/resources/priority.properties");
            FileInputStream propsInput = new FileInputStream(file);
            Properties prop = new Properties();
            prop.load(propsInput);

            this.upperBound = Math.max(Integer.parseInt(prop.getProperty("upperBound")), 0);    //we cannot set upperBound < 0
            this.lowerBound = Math.min(Integer.parseInt(prop.getProperty("lowerBound")), 0);    //we cannot set lowerBound > 0

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * This method calculates the variation of priority level for a doctor assigned to a specific concrete shift.
     * Then, it updates the temporary values of priority level.
     * @param allDoctorUffaPriority State of the doctors with the priority levels for the three queues
     * @param concreteShift Concrete shift which causes the variation of the temporary value of one priority level
     * @param pq Priority queue on which the temporary value of the priority level has to be updated
     */
    public void updatePriorityDoctors(List<DoctorUffaPriority> allDoctorUffaPriority, ConcreteShift concreteShift, PriorityQueueEnum pq){
        int priorityDelta;
        ContestoScocciatura contestoScocciatura;

        for(DoctorUffaPriority dup: allDoctorUffaPriority) {
            contestoScocciatura = new ContestoScocciatura(dup, concreteShift);
            priorityDelta = this.calcolaUffaComplessivoUtenteAssegnazione(contestoScocciatura);
            dup.updatePartialPriority(priorityDelta, pq, this.upperBound, this.lowerBound);

        }

    }


    /**
     * This method orders the doctors list (the DoctorUffaPriority list) on the base of the temporary value of the priority level of one queue.
     * @param allDoctorUffaPriority State of the doctors with the priority levels for the three queues
     * @param pq Priority queue on which the temporary value of the priority level has to be updated
     */
    public void orderByPriority(List<DoctorUffaPriority> allDoctorUffaPriority, PriorityQueueEnum pq){

        /*
         * We first shuffle the doctors list, and then we order it on the base of the uffa priority level.
         * This way the algorithm will extract randomly the users with the same uffa priority level.
         * It is possible because ordering algorithm is in place.
         */

        Collections.shuffle(allDoctorUffaPriority);
        //LONG_SHIFT and NIGHT consider first the own priority queue and, in case of same priority level in this queue, also the GENERAL queue.
        allDoctorUffaPriority.sort(Comparator.comparingInt(DoctorUffaPriority::getPartialGeneralPriority));

        switch(pq) {
            case LONG_SHIFT:
                allDoctorUffaPriority.sort(Comparator.comparingInt(DoctorUffaPriority::getPartialLongShiftPriority));

            case NIGHT:
                allDoctorUffaPriority.sort(Comparator.comparingInt(DoctorUffaPriority::getPartialNightPriority));

        }

    }


    /**
     * This method calculates the variation of the priority level for a doctor who has to be assigned to a specific concrete shift
     * considering all the annoyances.
     * @param contestoScocciatura Instance comprehending the useful information to calculate the right variation of uffa priority level
     * @return Total uffa priority variation due to the assignment to the concrete shift included in constestoScocciatura
     */
    public int calcolaUffaComplessivoUtenteAssegnazione(ContestoScocciatura contestoScocciatura){
        int uffa = 0;

        for(Scocciatura scocciatura: scocciature){
            uffa += scocciatura.calcolaUffa(contestoScocciatura);
        }

        return uffa;
    }


    /**
     * This method normalizes the priority level foreach doctor and foreach queue in a way such that the minimum priority level
     * for a particular queue turns 0 and other doctors' priority levels are subtracted by the same quantity.
     * @param allDoctorUffaPriority DoctorUffaPriority instances with the priority levels to be normalized
     */
    public void normalizeUffaPriority(List<DoctorUffaPriority> allDoctorUffaPriority) {

        int minGeneralPriority = this.upperBound;
        int minLongShiftPriority = this.upperBound;
        int minNightPriority = this.upperBound;

        //get the minimum priority values foreach queue
        for(DoctorUffaPriority dup : allDoctorUffaPriority) {
            if(dup.getGeneralPriority() < minGeneralPriority)
                minGeneralPriority = dup.getGeneralPriority();
            if(dup.getLongShiftPriority() < minLongShiftPriority)
                minLongShiftPriority = dup.getLongShiftPriority();
            if(dup.getNightPriority() < minNightPriority)
                minNightPriority = dup.getNightPriority();

        }

        //normalization of the priority level foreach doctor and foreach queue
        for(DoctorUffaPriority dup : allDoctorUffaPriority) {
            //we ensure that nobody will have a priority level > upperBound
            dup.setGeneralPriority(Math.min(dup.getGeneralPriority()-minGeneralPriority, this.upperBound));
            dup.setLongShiftPriority(Math.min(dup.getLongShiftPriority()-minLongShiftPriority, this.upperBound));
            dup.setNightPriority(Math.min(dup.getNightPriority()-minNightPriority, this.upperBound));

        }

    }

}
