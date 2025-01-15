package org.cswteams.ms3.enums;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;

import java.util.Map;
import java.util.stream.Collectors;

public enum ShiftState {

    COMPLETE(0),
    INCOMPLETE(1),
    INFEASIBLE(2);

    int shiftState;

    ShiftState(int shiftState) {
        this.shiftState = shiftState;
    }

    /**
     * Convert the <code>nameCategory</code> string into the corresponding <code>HolidayCategory</code>-
     *
     * @param shiftStateNum category name string
     * @return <code>HolidayCategory</code> enum object corresponding to <code>nameCategory</code>
     */
    public static ShiftState toShiftState(int shiftStateNum){
        for(ShiftState h: values()){
            if(h.shiftState == shiftStateNum){
                return  h;
            }
        }
        return null;
    }

    public static ShiftState extractStateFromConcreteShift(ConcreteShift concreteShift){
        int numberOfStructoredNeeded = 0;
        int numberOfSeniorNeeded = 0;
        int numberOfJuniorNeeded = 0;

        int numberOfStructoredAssigned = 0;
        int numberOfSeniorAssigned = 0;
        int numberOfJuniorAssigned = 0;
        for(QuantityShiftSeniority qss: concreteShift.getShift().getQuantityShiftSeniority()){
            for(Map.Entry<Seniority,Integer> entry : qss.getSeniorityMap().entrySet()) {
                switch (entry.getKey()){
                    case STRUCTURED:
                        numberOfStructoredNeeded += entry.getValue();
                        break;
                    case SPECIALIST_SENIOR:
                        numberOfSeniorNeeded += entry.getValue();
                        break;
                    case SPECIALIST_JUNIOR:
                        numberOfJuniorNeeded += entry.getValue();
                        break;
                }

            }
        }
        numberOfStructoredAssigned = (int) concreteShift.getDoctorAssignmentList()
                .stream().filter(da -> da.getDoctor().getSeniority() == Seniority.STRUCTURED).count();

        numberOfSeniorAssigned = (int) concreteShift.getDoctorAssignmentList()
                .stream().filter(da -> da.getDoctor().getSeniority() == Seniority.SPECIALIST_SENIOR).count();

        numberOfJuniorAssigned = (int) concreteShift.getDoctorAssignmentList()
                .stream().filter(da -> da.getDoctor().getSeniority() == Seniority.SPECIALIST_JUNIOR).count();

        // check status
        if(numberOfStructoredNeeded*2 == numberOfStructoredAssigned){
            if(numberOfSeniorNeeded*2 == numberOfSeniorAssigned && numberOfJuniorNeeded*2 == numberOfJuniorAssigned)
                return ShiftState.COMPLETE;
            return ShiftState.INCOMPLETE;
        }
        return ShiftState.INFEASIBLE;

    }
}
