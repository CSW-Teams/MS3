package org.cswteams.ms3.jpa_constraints.temporal_consistency;

import java.time.LocalDate;

public class DateTimeComparator implements Comparator {
    @Override
    public boolean compare(Object first, Object second) {
        return ((LocalDate) first).isBefore((LocalDate) second) ;
    }
}
