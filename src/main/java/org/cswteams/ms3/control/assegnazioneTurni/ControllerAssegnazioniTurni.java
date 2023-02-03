package org.cswteams.ms3.control.assegnazioneTurni;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.ScheduleDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.ModificaAssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Schedule;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public class ControllerAssegnazioniTurni implements IControllerAssegnazioneTurni{
    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private  TurnoDao turnoDao;

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniAssegnati()  {
        Set<AssegnazioneTurno> turniSet = new HashSet<>(assegnazioneTurnoDao.findAll());
        return MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turniSet);
    }

    @Override
    public AssegnazioneTurno creaTurnoAssegnato(@NotNull RegistraAssegnazioneTurnoDTO dto) throws AssegnazioneTurnoException {

        Turno turno = turnoDao.findAllByServizioNomeAndTipologiaTurno(dto.getServizio().getNome(), dto.getTipologiaTurno()).get(0);
        if(turno == null)
            throw new AssegnazioneTurnoException("Non esiste un turno con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia turno: "+dto.getTipologiaTurno().toString());

        AssegnazioneTurno assegnazioneTurno= new AssegnazioneTurno(LocalDate.of(dto.getAnno(),dto.getMese(),dto.getGiorno()),turno, MappaUtenti.utenteDTOtoEntity(dto.getUtentiReperibili()),MappaUtenti.utenteDTOtoEntity(dto.getUtentiDiGuardia()));

        if(!checkAssegnazioneTurno(assegnazioneTurno)){
            throw new AssegnazioneTurnoException("Collisione tra utenti reperibili e di guardia");
        }
        return assegnazioneTurnoDao.save(assegnazioneTurno);
    }


    @Override
    public Set<AssegnazioneTurnoDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<AssegnazioneTurno> turniAllocatiERiserve = assegnazioneTurnoDao.findTurniUtente(idPersona);
        Set<AssegnazioneTurnoDTO> turniAllocati = new HashSet<>();
        for(AssegnazioneTurno assegnazioneTurno: turniAllocatiERiserve){
            if(assegnazioneTurno.getTurno().isReperibilitaAttiva() || !utenteInReperibilita(assegnazioneTurno, idPersona))
                turniAllocati.add(MappaAssegnazioneTurni.assegnazioneTurnoToDTO(assegnazioneTurno));
        }
        return turniAllocati;
    }

    private boolean utenteInReperibilita(AssegnazioneTurno assegnazioneTurno, Long idPersona){
        for(Utente utenteReperibile: assegnazioneTurno.getUtentiReperibili()){
            if(utenteReperibile.getId().longValue() == idPersona.longValue())
                return true;
        }
        return false;
    }


    @Override
    public AssegnazioneTurno leggiTurnoByID(long idAssegnazione) {
        return assegnazioneTurnoDao.findById(idAssegnazione).get();
    }


    private boolean checkAssegnazioneTurno(AssegnazioneTurno turno) {

        for(Utente utente1: turno.getUtentiDiGuardia()){
            for(Utente utente2: turno.getUtentiReperibili()){
                if (utente1.getId().longValue() == utente2.getId().longValue()){
                    return false;
                }
            }
        }
        return true;
    }

}
