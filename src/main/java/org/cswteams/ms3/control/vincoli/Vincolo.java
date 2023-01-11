package org.cswteams.ms3.control.vincoli;

import org.cswteams.ms3.exception.ViolatedConstraintException;

public interface Vincolo {
    
    /** @throws ViolatedConstraintException : se il vincolo è violato */
    void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException;
}
