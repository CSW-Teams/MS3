package org.cswteams.ms3.control.richiestaRimozioneDaTurno;

import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
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
     * Lettura di tutte le richieste di rimozione da turno, filtrate per id utente
     *
     * @param utenteId id utente per il filtraggio
     * @return lista di oggetti relativi alle richieste di rimozione da turno per l'utente specificato
     */
    Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurnoPerUtente(Long utenteId);

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
     * @param richiestaRimozioneDaTurnoDTO dto dell'oggetto RichiestaRimozioneDaTurno
     * @return oggetto relativo alla richiesta di rimozione da turno, opportunamente aggiornato con l'esito fornito
     * @throws DatabaseException in caso di errori durante la ricerca di dati in base dati
     */
    RichiestaRimozioneDaTurno risolviRichiestaRimozioneDaTurno(RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException, AssegnazioneTurnoException;

    /**
     * Permette il caricamento di un allegato (facoltativo) per una data richiesta di rimozione da turno assegnato.
     *
     * @param idRichiestaRimozioneDaTurno id della richiesta di rimozione da turno alla quale aggiungere l'allegato
     * @param allegato                    file da allegare
     * @return richiesta rimozione da turno aggiornata con il file allegato
     * @throws IOException       in caso di errori di I/O
     * @throws DatabaseException in caso di errori durante la ricerca di dati in base dati
     */
    RichiestaRimozioneDaTurno caricaAllegato(@NotNull Long idRichiestaRimozioneDaTurno, @NotNull MultipartFile allegato) throws IOException, DatabaseException;
}
