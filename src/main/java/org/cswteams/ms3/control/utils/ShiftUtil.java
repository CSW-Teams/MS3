package org.cswteams.ms3.control.utils;

import org.cswteams.ms3.entity.QuantityShiftSeniority;
import org.cswteams.ms3.entity.Shift;

public class ShiftUtil {

    /**
     * This method retrieves the total number of the doctors required for a specific shift (specialized + structured)
     * @param shift Shift in which we have to count the total number of required doctors.
     * @return The number of required doctors.
     */
    public static int getNumRequiredDoctors(Shift shift) {
        int numRequiredDoctors = 0;

        for(QuantityShiftSeniority qss : shift.getQuantityShiftSeniority()) {
            numRequiredDoctors += qss.getQuantity();

        }
        return numRequiredDoctors;

    }

}
