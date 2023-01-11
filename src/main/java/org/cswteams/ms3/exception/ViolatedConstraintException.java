package org.cswteams.ms3.exception;

public class ViolatedConstraintException extends Exception{

    public ViolatedConstraintException(Exception cause) {
        super(cause);
    }

    public ViolatedConstraintException(String message) {
        super(message);
    }

    public ViolatedConstraintException(){  
        super();
     }

}
