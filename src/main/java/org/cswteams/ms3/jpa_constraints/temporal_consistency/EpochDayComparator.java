package org.cswteams.ms3.jpa_constraints.temporal_consistency;

public class EpochDayComparator implements Comparator{
    @Override
    public boolean compare(Object first, Object second) {
        long first_num = (Long) first ;
        long second_num = (Long) second ;

        return first_num < second_num ;
    }
}
