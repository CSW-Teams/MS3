package org.cswteams.ms3.control.scocciatura;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.DoctorUffaPriority;
import org.cswteams.ms3.entity.scocciature.ContestoScocciatura;
import org.cswteams.ms3.entity.scocciature.Scocciatura;
import org.cswteams.ms3.enums.PriorityQueueEnum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Math;
import java.util.*;

/**
 * Gestisce tutti gli aspetti relativi ai livelli di priorità "uffa" (o "scocciatura") dei medici.
 * Questo controller è responsabile del calcolo, dell'aggiornamento, dell'ordinamento e della
 * normalizzazione delle priorità dei medici in base alle assegnazioni dei turni.
 *
 * Fa parte della "Pipeline priorità (UFFA/scocciatura)" (Microtask 1.2).
 *
 * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
 */
public class ControllerScocciatura {

    public List<Scocciatura> scocciature;   //why public!?
    private final int upperBound;
    private final int lowerBound;


    /**
     * Costruttore del {@code ControllerScocciatura}. Inizializza le scocciature e legge i valori
     * di {@code upperBound} e {@code lowerBound} per i livelli di priorità dal file di configurazione
     * {@code priority.properties}.
     *
     * @param scocciature Lista delle {@link Scocciatura scocciature} da gestire.
     * @throws RuntimeException se il file {@code priority.properties} non può essere letto.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
     */
    public ControllerScocciatura(List<Scocciatura> scocciature) {
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
     * Calcola la variazione del livello di priorità ("uffa") per ciascun {@link Doctor medico}
     * in relazione all'assegnazione a un {@link ConcreteShift turno concreto}.
     * Successivamente, aggiorna i valori "parziali" della priorità per la coda specificata.
     *
     * Questo metodo è chiamato per calcolare un delta di "uffa" per ciascun medico,
     * aggiornando i valori parziali della coda interessata (Microtask 1.2).
     *
     * @param allDoctorUffaPriority Stato dei medici con i livelli di priorità per le tre code.
     * @param concreteShift {@link ConcreteShift Turno concreto} che causa la variazione del valore temporaneo di una priorità.
     * @param pq La {@link PriorityQueueEnum coda di priorità} su cui aggiornare il valore temporaneo della priorità.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
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
     * Ordina la lista dei medici (lista di {@link DoctorUffaPriority}) in base al valore
     * temporaneo del livello di priorità di una specifica coda.
     *
     * @param allDoctorUffaPriority Stato dei medici con i livelli di priorità per le tre code.
     * @param pq La {@link PriorityQueueEnum coda di priorità} su cui basare l'ordinamento.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
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
                break;

            case NIGHT:
                allDoctorUffaPriority.sort(Comparator.comparingInt(DoctorUffaPriority::getPartialNightPriority));
                break;
        }

    }


    /**
     * Calcola la variazione complessiva del livello di priorità ("uffa") per un medico
     * che deve essere assegnato a un {@link ConcreteShift turno concreto}, considerando tutte le {@link Scocciatura scocciature} applicabili.
     *
     * Le "scocciature" che generano i delta sono entità persistenti: penalità per giorno/time slot
     * (es. weekend o fasce specifiche), per desiderata non rispettate, e per festività/fasce orarie.
     * Il delta complessivo è la somma dei pesi delle scocciature applicabili al contesto corrente (Microtask 1.2).
     *
     * @param contestoScocciatura Istanza {@link ContestoScocciatura} che comprende le informazioni
     *                            utili per calcolare la variazione di priorità.
     * @return Variazione totale della priorità ("uffa") dovuta all'assegnazione al turno concreto.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
     */
    public int calcolaUffaComplessivoUtenteAssegnazione(ContestoScocciatura contestoScocciatura){
        int uffa = 0;

        for(Scocciatura scocciatura: scocciature){
            uffa += scocciatura.calcolaUffa(contestoScocciatura);
        }

        return uffa;
    }


    /**
     * Normalizza il livello di priorità per ogni medico e per ogni coda, in modo che il livello
     * di priorità minimo per una particolare coda diventi 0 e i livelli di priorità degli altri
     * medici vengano sottratti della stessa quantità.
     *
     * Questo assicura che il minimo diventi 0, se il controller scocciatura è disponibile
     * (Microtask 1.2).
     *
     * @param allDoctorUffaPriority {@link DoctorUffaPriority Istanza di DoctorUffaPriority}
     *                              con i livelli di priorità da normalizzare.
     * @see docs/AI_powered_rescheduling/sprint_4/story_1.md#microtask-12--vincoli-e-pipeline-priorità-baseline
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
