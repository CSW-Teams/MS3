package org.cswteams.ms3.entity.vincoli;

import org.cswteams.ms3.exception.ViolatedConstraintException;

/**
 * Questo vincolo impone a un utente di possedere le categorie elencate
 */
public class VincoloRichiestaCategoriaUtente extends Vincolo{


    @Override
    public void verificaVincolo(ContestoVincolo contesto) throws ViolatedConstraintException {

    }
}
