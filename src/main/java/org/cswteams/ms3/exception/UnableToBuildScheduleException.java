package org.cswteams.ms3.exception;

public class UnableToBuildScheduleException extends Exception{

    public UnableToBuildScheduleException(String string, NotEnoughFeasibleUsersException e) {
        super(string, e);
    }

}
