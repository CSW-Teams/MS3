package org.cswteams.ms3.control.richiestaRimozioneDaTurno;

import org.cswteams.ms3.control.utils.MappaRichiestaRimozioneDaTurno;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.RichiestaRimozioneDaTurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.RichiestaRimozioneDaTurno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

@Service
public class ControllerRichiestaRimozioneDaTurno implements IControllerRichiestaRimozioneDaTurno {

    @Autowired
    private RichiestaRimozioneDaTurnoDao richiestaRimozioneDaTurnoDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private UtenteDao utenteDao;

    @Override
    public RichiestaRimozioneDaTurno creaRichiestaRimozioneDaTurno(@NotNull RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException {
        // 1. ricerca AssegnazioneTurno --------------------------------------------------------------
        Long assegnazioneTurnoId = richiestaRimozioneDaTurnoDTO.getAssegnazioneTurnoId();
        if (assegnazioneTurnoId == null) {
            throw new NullPointerException("Id AssegnazioneTurno non valida");
        }
        Optional<AssegnazioneTurno> assegnazioneTurno = assegnazioneTurnoDao.findById(assegnazioneTurnoId);
        if (assegnazioneTurno.isEmpty()) {
            throw new DatabaseException("AssegnazioneTurno non trovata per id = " + assegnazioneTurnoId);
        }

        // 2. ricerca Utente -------------------------------------------------------------------------
        Long utenteId = richiestaRimozioneDaTurnoDTO.getUtenteId();
        if (utenteId == null) {
            throw new NullPointerException("Id Utente non valido");
        }
        Optional<Utente> utente = utenteDao.findById(utenteId);
        if (utente.isEmpty()) {
            throw new DatabaseException("AssegnazioneTurno non trovata per id = " + assegnazioneTurnoId);
        }

        // 3. chiamata a metodo interno -----------------------------------------------------------
        return this._creaRichiestaRimozioneDaTurno(assegnazioneTurno.get(), utente.get(), richiestaRimozioneDaTurnoDTO.getDescrizione());
    }

    @Override
    public RichiestaRimozioneDaTurno _creaRichiestaRimozioneDaTurno(@NotNull AssegnazioneTurno assegnazioneTurno, @NotNull Utente utente, @NotNull String descrizione) {
        if (richiestaRimozioneDaTurnoDao.checkIfAlreadyPresent(assegnazioneTurno.getId(), utente.getId())) {
            throw new RuntimeException("Esiste già una richiesta di rimozione da turno assegnato per l'utente " + utente + " per il la assegnazione " + assegnazioneTurno);
        }
        if (!assegnazioneTurno.isAllocated(utente) && !assegnazioneTurno.isReserve(utente)) {
            throw new RuntimeException("L'utente " + utente + " non risulta essere coinvolto nella assegnazione turno " + assegnazioneTurno);
        }
        RichiestaRimozioneDaTurno richiestaRimozioneDaTurno = new RichiestaRimozioneDaTurno(assegnazioneTurno, utente, descrizione);
        richiestaRimozioneDaTurnoDao.saveAndFlush(richiestaRimozioneDaTurno);
        return richiestaRimozioneDaTurno;
    }

    @Override
    public Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurno() {
        return MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoEntitytoDTO(richiestaRimozioneDaTurnoDao.findAll());
    }

    @Override
    public Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurnoPendenti() {
        return MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoEntitytoDTO(richiestaRimozioneDaTurnoDao.findAllPending());
    }

    @Override
    public Optional<RichiestaRimozioneDaTurnoDTO> leggiRichiestaRimozioneDaTurno(Long idRichiesta) {
        Optional<RichiestaRimozioneDaTurno> r = richiestaRimozioneDaTurnoDao.findById(idRichiesta.toString());
        return r.map(MappaRichiestaRimozioneDaTurno::richiestaRimozioneDaTurnoToDTO);
    }

    @Override
    public RichiestaRimozioneDaTurno risolviRichiestaRimozioneDaTurno(Long idRichiesta, boolean esito) throws DatabaseException {
        Optional<RichiestaRimozioneDaTurno> r = richiestaRimozioneDaTurnoDao.findById(idRichiesta.toString());
        if (r.isEmpty()) {
            throw new DatabaseException("RichiestaRimozioneDaTurno non trovata per id = " + idRichiesta);
        }
        if (r.get().isEsaminata()) {
            throw new DatabaseException("La RichiestaRimozioneDaTurno avente id = " + idRichiesta + " risulta essere già stata esaminata");
        }
        if (esito) {
            AssegnazioneTurno assegnazioneTurno = r.get().getAssegnazioneTurno();
            assegnazioneTurno.retireAssignedUser(r.get().getUtente());
            assegnazioneTurnoDao.saveAndFlush(assegnazioneTurno);
            r.get().setEsito(true);
        } else {
            r.get().setEsito(false);
        }
        r.get().setEsaminata(true);
        richiestaRimozioneDaTurnoDao.saveAndFlush(r.get());
        return r.get();
    }
}
