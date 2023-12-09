package org.cswteams.ms3.control.richiestaRimozioneDaTurno;

import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;

import java.util.Optional;
import java.util.Set;

public interface IControllerRichiestaRimozioneDaTurno {

    /**
     * Registra una nuova richiesta di rimozione da turno, dato un oggetto DTO.
     *
     * @param richiestaRimozioneDaTurnoDTO DTO relativo all'oggetto richiesta
     * @return oggetto <code>RichiestaRimozioneDaTurno</code> relativo alla richiesta
     * @throws DatabaseException          in caso di errori durante la ricerca di dati in base dati
     * @throws AssegnazioneTurnoException in caso di errori durante la ricerca di dati in base dati
     */
    RichiestaRimozioneDaTurno creaRichiestaRimozioneDaTurno(RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException, AssegnazioneTurnoException;

    /**
     * Registra una nuova richiesta di rimozione da turno, dati l'utente richiedente ed il turno per il quale si fa richiesta.
     *
     * @param assegnazioneTurno il turno per il quale si chiede la rimozione
     * @param utente            l'utente facente richiesta di rimozione
     * @param descrizione       motivazione addotta alla richiesta di rimozione dal turno
     * @return oggetto <code>RichiestaRimozioneDaTurno</code> relativo alla richiesta
     * @throws DatabaseException          in caso di errori durante la ricerca di dati in base dati
     * @throws AssegnazioneTurnoException in caso di errori durante la ricerca di dati in base dati
     */
    RichiestaRimozioneDaTurno _creaRichiestaRimozioneDaTurno(AssegnazioneTurno assegnazioneTurno, Utente utente, String descrizione) throws DatabaseException, AssegnazioneTurnoException;

    /**
     * Lettura di tutte le richieste di rimozione da turno presenti in base dati.
     *
     * @return lista di oggetti relativi alle richieste di rimozione da turno presenti in base dati
     */
    Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurno();

    /**
     * Lettura di tutte le richieste di rimozione da turno pendenti (i.e. non esaminate) presenti in base dati.
     *
     * @return lista di oggetti relativi alle richieste di rimozione da turno pendenti presenti in base dati
     */
    Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurnoPendenti();

    /**
     * Lettura di una specifica richiesta di rimozione da turno, dato un id.
     *
     * @param idRichiesta id della richiesta da prelevare da base dati.
     * @return oggetto relativo alla richiesta cercata, ove presente
     */
    Optional<RichiestaRimozioneDaTurnoDTO> leggiRichiestaRimozioneDaTurno(Long idRichiesta);

    /**
     * Assegnazione dell'esito della richiesta di rimozione da turno assegnato, da parte del <i>Pianificatore</i>.
     *
     * @param idRichiesta id della richiesta da prelevare da base dati
     * @param esito       <code>true</code> se il <i>Pianificatore</i> approva la richiesta, <code>false</code> altrimenti
     * @return oggetto relativo alla richiesta di rimozione da turno, opportunamente aggiornato con l'esito fornito
     * @throws DatabaseException in caso di errori durante la ricerca di dati in base dati
     */
    RichiestaRimozioneDaTurno risolviRichiestaRimozioneDaTurno(Long idRichiesta, boolean esito) throws DatabaseException;
}
