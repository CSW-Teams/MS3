package org.cswteams.ms3.exception;

import java.time.LocalDate;

public class IllegalScheduleException extends Exception{
    /**
     * Constructor for the exception thrown when trying to build a schedule with incoerenti dates
     */
    public IllegalScheduleException(){
        super("[ERROR] Wrong parameters passed to start the initialization of a new schedule, in particular startDate is after or equal to endDate");
    }

    public IllegalScheduleException(String message) {
        super(message);
    }
}
