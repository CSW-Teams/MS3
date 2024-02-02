package org.cswteams.ms3.entity.scheduling.algo;

import lombok.Getter;
import lombok.Setter;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Schedule;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public abstract class DoctorXY {


    @Id
    @GeneratedValue
    protected Long id;

    /**
     * Utente a cui appartiene questo stato
     */
    @ManyToOne
    protected Doctor doctor;

    /**
     * Pianificazione a cui appartiene questo stato
     */
    @OneToOne
    protected Schedule schedule;

    /** All the concrete shifts assigned to the doctor in the current schedule */
    @Transient
    List<ConcreteShift> assegnazioniTurnoCache;

    public abstract List<ConcreteShift> getAssegnazioniTurnoCache();


    // SOLO PER UFFAPOINTS
    protected int uffaParziale = 0;
    protected int uffaCumulativo = 0;

    /**
     * Aggiunge in ordine la nuova assegnazione alla lista delle assegnazioni dell'utente
     **/
    public void addConcreteShift(ConcreteShift newConcreteShift) {
        List<ConcreteShift> concreteShiftList = getAssegnazioniTurnoCache();
        int idInsert = concreteShiftList.size();
        for (int i = 0; i < concreteShiftList.size(); i++) {
            if (concreteShiftList.get(i).getDate() > newConcreteShift.getDate() || concreteShiftList.get(i).getDate() == newConcreteShift.getDate()) {
                if (concreteShiftList.get(i).getShift().getStartTime().isAfter(newConcreteShift.getShift().getStartTime())) {
                    idInsert = i;
                }
            }
        }
        concreteShiftList.add(idInsert, newConcreteShift);
    }

    public void saveUffaTemp() {
        this.uffaCumulativo = this.uffaParziale;
    }

    public void addUffaTemp(int uffa) {
        this.uffaParziale = this.uffaCumulativo + uffa;
    }




}