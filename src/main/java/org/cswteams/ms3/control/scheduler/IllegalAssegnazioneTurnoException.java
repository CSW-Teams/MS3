package org.cswteams.ms3.control.scheduler;

public class IllegalAssegnazioneTurnoException extends Exception{

    public IllegalAssegnazioneTurnoException(Exception cause){
        super(cause);
    }

    public IllegalAssegnazioneTurnoException(String message){
        super(message);
    }

}
