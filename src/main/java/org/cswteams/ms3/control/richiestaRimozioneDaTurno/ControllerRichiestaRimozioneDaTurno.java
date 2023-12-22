package org.cswteams.ms3.control.richiestaRimozioneDaTurno;

import org.cswteams.ms3.control.concreteShift.IConcreteShiftController;
import org.cswteams.ms3.control.utils.MappaRichiestaRimozioneDaTurno;
import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.RemovalFromShiftRequestDAO;
import org.cswteams.ms3.dao.UserDAO;
import org.cswteams.ms3.dto.RichiestaRimozioneDaTurnoDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.RequestRemovalFromConcreteShift;
import org.cswteams.ms3.entity.User;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Service
public class ControllerRichiestaRimozioneDaTurno implements IControllerRichiestaRimozioneDaTurno {

    @Autowired
    private RemovalFromShiftRequestDAO removalFromShiftRequestDAO;

    @Autowired
    private IConcreteShiftController controllerAssegnazioneTurni;

    @Autowired
    private ConcreteShiftDAO assegnazioneTurnoDao;

    @Autowired
    private UserDAO utenteDao;

    @Override
    public RequestRemovalFromConcreteShift creaRichiestaRimozioneDaTurno(@NotNull RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException, AssegnazioneTurnoException {
        // 1. ricerca AssegnazioneTurno --------------------------------------------------------------
        /*Long assegnazioneTurnoId = richiestaRimozioneDaTurnoDTO.getIdAssegnazioneTurno();
        if (assegnazioneTurnoId == null) {
            throw new DatabaseException("Id AssegnazioneTurno non valida");
        }
        Optional<ConcreteShift> assegnazioneTurno = assegnazioneTurnoDao.findById(assegnazioneTurnoId);
        if (assegnazioneTurno.isEmpty()) {
            throw new DatabaseException("AssegnazioneTurno non trovata per id = " + assegnazioneTurnoId);
        }

        // 2. ricerca Utente -------------------------------------------------------------------------
        Long utenteRichiedenteId = richiestaRimozioneDaTurnoDTO.getIdUtenteRichiedente();
        if (utenteRichiedenteId == null) {
            throw new DatabaseException("Id Utente non valido");
        }
        Optional<User> utenteRichiedente = utenteDao.findById(utenteRichiedenteId);
        if (utenteRichiedente.isEmpty()) {
            throw new DatabaseException("AssegnazioneTurno non trovata per id = " + assegnazioneTurnoId);
        }

        // 3. chiamata a metodo interno -----------------------------------------------------------
        return this._creaRichiestaRimozioneDaTurno(assegnazioneTurno.get(), utenteRichiedente.get(), richiestaRimozioneDaTurnoDTO.getDescrizione());
        */
        return null;
    }


    @Override
    public RequestRemovalFromConcreteShift _creaRichiestaRimozioneDaTurno(@NotNull ConcreteShift assegnazioneTurno, @NotNull User utenteRichiedente, @NotNull String descrizione) throws DatabaseException, AssegnazioneTurnoException {
        /*if (!removalFromShiftRequestDAO.findAllByAssegnazioneTurnoIdAndUtenteId(assegnazioneTurno.getId(), utenteRichiedente.getId()).isEmpty()) {
            throw new DatabaseException("Esiste già una richiesta di rimozione da turno assegnato per l'utente " + utenteRichiedente + " per il la assegnazione " + assegnazioneTurno);
        }
        if (!assegnazioneTurno.isAllocated(utenteRichiedente) && !assegnazioneTurno.isReserve(utenteRichiedente)) {
            throw new AssegnazioneTurnoException("L'utente " + utenteRichiedente + " non risulta essere coinvolto nella assegnazione turno " + assegnazioneTurno);
        }
        RequestRemovalFromConcreteShift richiestaRimozioneDaTurno = new RichiestaRimozioneDaTurno(assegnazioneTurno, utenteRichiedente, descrizione);

        removalFromShiftRequestDAO.saveAndFlush(richiestaRimozioneDaTurno);
        return richiestaRimozioneDaTurno;*/
            return null;
    }



    @Override
    public Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurno() {
        //return MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoEntitytoDTO(removalFromShiftRequestDAO.findAll());
        return null;
    }

    @Override
    public Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurnoPendenti() {
        //return MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoEntitytoDTO(removalFromShiftRequestDAO.findAllPending());
        return null;
    }

    @Override
    @Transactional
    public Set<RichiestaRimozioneDaTurnoDTO> leggiRichiesteRimozioneDaTurnoPerUtente(Long utenteId) {
        //return MappaRichiestaRimozioneDaTurno.richiestaRimozioneDaTurnoEntitytoDTO(removalFromShiftRequestDAO.findAllByUser(utenteId));
        return null;
    }

    @Override
    public Optional<RichiestaRimozioneDaTurnoDTO> leggiRichiestaRimozioneDaTurno(Long idRichiesta) {
        /*Optional<RequestRemovalFromConcreteShift> r = removalFromShiftRequestDAO.findById(idRichiesta);
        return r.map(MappaRichiestaRimozioneDaTurno::richiestaRimozioneDaTurnoToDTO);*/
        return null;
    }

    @Override
    public RequestRemovalFromConcreteShift risolviRichiestaRimozioneDaTurno(RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException, AssegnazioneTurnoException {
        return null;
    }

    @Override
    public RequestRemovalFromConcreteShift caricaAllegato(Long idRichiestaRimozioneDaTurno, MultipartFile allegato) throws IOException, DatabaseException {
        return null;
    }
/*
    @Override
    public RichiestaRimozioneDaTurno risolviRichiestaRimozioneDaTurno(RichiestaRimozioneDaTurnoDTO richiestaRimozioneDaTurnoDTO) throws DatabaseException, AssegnazioneTurnoException {
        Optional<RichiestaRimozioneDaTurno> r = removalFromShiftRequestDAO.findById(richiestaRimozioneDaTurnoDTO.getIdRichiestaRimozioneDaTurno());
        if (r.isEmpty()) {
            throw new DatabaseException("RichiestaRimozioneDaTurno non trovata per id = " + richiestaRimozioneDaTurnoDTO.getIdRichiestaRimozioneDaTurno());
        }
        if (r.get().isEsaminata()) {
            throw new RuntimeException("La RichiestaRimozioneDaTurno avente id = " + r.get().getIdRichiestaRimozioneDaTurno() + " risulta essere già stata esaminata");
        }
        if (richiestaRimozioneDaTurnoDTO.isEsito()) {
            AssegnazioneTurno assegnazioneTurno = r.get().getAssegnazioneTurno();
            Optional<Utente> richiedente = utenteDao.findById(richiestaRimozioneDaTurnoDTO.getIdUtenteRichiedente());
            if (richiedente.isEmpty()) {
                throw new DatabaseException("Utente richiedente non trovato per id = " + richiestaRimozioneDaTurnoDTO.getIdUtenteRichiedente());
            }
            Optional<Utente> sostituto = utenteDao.findById(richiestaRimozioneDaTurnoDTO.getIdUtenteSostituto());
            if (sostituto.isEmpty()) {
                throw new DatabaseException("Utente sostituto non trovato per id = " + richiestaRimozioneDaTurnoDTO.getIdUtenteSostituto());
            }
            try {
                controllerAssegnazioneTurni.sostituisciUtenteAssegnato(assegnazioneTurno, richiedente.get(), sostituto.get());
            } catch (AssegnazioneTurnoException e) {
                throw new AssegnazioneTurnoException("Impossibile sostituire l'utente " + richiedente.get() + " con l'utente " + sostituto.get() + " per la assegnazione turno " + assegnazioneTurno);
            }
            r.get().setEsito(true);
            r.get().setUtenteSostituto(sostituto.get());
        } else {
            r.get().setEsito(false);
        }
        r.get().setEsaminata(true);
        removalFromShiftRequestDAO.saveAndFlush(r.get());
        return r.get();
    }

    @Override
    @Transactional
    public RichiestaRimozioneDaTurno caricaAllegato(@NotNull Long idRichiestaRimozioneDaTurno, @NotNull MultipartFile allegato) throws IOException, DatabaseException {
        Optional<RichiestaRimozioneDaTurno> r = removalFromShiftRequestDAO.findById(idRichiestaRimozioneDaTurno);
        if (r.isEmpty()) {
            throw new DatabaseException("RichiestaRimozioneDaTurno non trovata per id = " + idRichiestaRimozioneDaTurno);
        }
        r.get().setAllegato(allegato.getBytes());
        removalFromShiftRequestDAO.saveAndFlush(r.get());
        return r.get();
    }*/
}
