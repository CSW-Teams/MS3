package org.cswteams.ms3.control.vincoli;

public interface Vincolo {
    
    /** @throws ViolatedConstraintException: se il vincolo è violato */
    void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException;
}
