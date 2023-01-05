package org.cswteams.ms3.control.scheduler;

public class NotEnoughFeasibleUsersException extends Exception {

    public NotEnoughFeasibleUsersException(int expected, int actual) {
        super(String.format("Needed %d users, but only %d are feasible", expected, actual));
    }
    
}
