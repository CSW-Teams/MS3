package org.cswteams.ms3.exception;

public class NotEnoughFeasibleUsersException extends UnableToBuildScheduleException {

    public NotEnoughFeasibleUsersException(int expected, int actual) {
        super(String.format("Needed %d users, but only %d are feasible", expected, actual));
    }
    
}
