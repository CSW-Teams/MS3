package org.cswteams.ms3.control.scambioTurno;

import org.cswteams.ms3.dao.AssegnazioneTurnoDao;
import org.cswteams.ms3.dao.RequestTurnChangeDao;
import org.cswteams.ms3.dao.UtenteDao;
import org.cswteams.ms3.dto.RequestTurnChangeDto;
import org.cswteams.ms3.dto.ViewUserTurnRequestsDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Request;
import org.cswteams.ms3.entity.Utente;
import org.cswteams.ms3.enums.RequestENUM;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ControllerScambioTurno implements IControllerScambioTurno {

    @Autowired
    private UtenteDao utenteDao;

    @Autowired
    private AssegnazioneTurnoDao assegnazioneTurnoDao;

    @Autowired
    private RequestTurnChangeDao requestTurnChangeDao;

    /**
     * Questo metodo crea una richiesta di modifica turno.
     * @param requestTurnChangeDto
     * @return
     */
    @Override
    @Transactional
    public void requestTurnChange(@NotNull RequestTurnChangeDto requestTurnChangeDto) throws AssegnazioneTurnoException {
        Optional<AssegnazioneTurno> assegnazioneTurno= assegnazioneTurnoDao.findById(requestTurnChangeDto.getConcreteShiftId());
        if(assegnazioneTurno.isEmpty()){
            throw new AssegnazioneTurnoException("Turno non presente");
        }

        Optional<Utente> senderOptional = Optional.ofNullable(utenteDao.findById(requestTurnChangeDto.getSenderId()));
        if(senderOptional.isEmpty()){
            throw new AssegnazioneTurnoException("Utente richiedente non presente nel database");
        }


        Optional<Utente> receiverOptional = Optional.ofNullable(utenteDao.findById(requestTurnChangeDto.getReceiverId()));
        if(receiverOptional.isEmpty()){
            throw new AssegnazioneTurnoException("Utente richiesto non presente nel database");
        }

        AssegnazioneTurno concreteShift = assegnazioneTurno.get();

        List<Long> userDiGuardiaIds = concreteShift.getUtentiDiGuardia().stream()
                .map(Utente::getId)
                .collect(Collectors.toList());

        List<Long> userReperibiliIds = concreteShift.getUtentiReperibili().stream()
                .map(Utente::getId)
                .collect(Collectors.toList());

        if(!userDiGuardiaIds.contains(requestTurnChangeDto.getSenderId()) && !userReperibiliIds.contains(requestTurnChangeDto.getSenderId())){
            throw new AssegnazioneTurnoException("Utente richiedente non assegnato al turno");
        }

        if(userDiGuardiaIds.contains(requestTurnChangeDto.getReceiverId()) || userReperibiliIds.contains(requestTurnChangeDto.getReceiverId())){
            throw new AssegnazioneTurnoException("Utente richiesto già assegnato al turno");
        }



        Request request = new Request(senderOptional.get(), receiverOptional.get(), concreteShift);

        List<Request> requests = requestTurnChangeDao.findBySenderIdAndTurnIdAndStatus(requestTurnChangeDto.getSenderId(), requestTurnChangeDto.getConcreteShiftId(), RequestENUM.PENDING);

        if(!requests.isEmpty()){
            throw new AssegnazioneTurnoException("esiste già una richiesta in corso per la modifica di questo turno");
        }

        try {
            requestTurnChangeDao.saveAndFlush(request);
        } catch(ConstraintViolationException e){
            throw new AssegnazioneTurnoException("esiste già un cambio pendente");
        }
    }

    private List<Long> dateAndTimeToEpoch(LocalDate startDate, LocalTime startTime, Duration duration){
        ZoneId gmtZone = ZoneId.of("GMT");

        LocalDateTime localDateTimeInizio = LocalDateTime.of(startDate, startTime);
        Instant inizioInstant = localDateTimeInizio.atZone(gmtZone).toInstant();
        Instant fineInstant = inizioInstant.plus(duration);

        List<Long> l = new ArrayList<>();
        l.add(inizioInstant.getEpochSecond());
        l.add(fineInstant.getEpochSecond());

        return l;
    }

    @Override
    @Transactional
    public List<ViewUserTurnRequestsDTO> getRequestsBySender(@NotNull Long id){
        //TODO check if user exists
        List<Request> requests = requestTurnChangeDao.findBySenderId(id);
        List<ViewUserTurnRequestsDTO> dtos = new ArrayList<>();
        for(Request r : requests){
            long requestId = r.getId();

            List<Long> list = dateAndTimeToEpoch(r.getTurn().getData(), r.getTurn().getTurno().getOraInizio(), r.getTurn().getTurno().getDurata());
            long inizioEpoch = list.get(0);
            long fineEpoch = list.get(1);

            String turnDescription = r.getTurn().getTurno().getMansione()+" in "+r.getTurn().getTurno().getServizio().getNome();
            String userDetails = r.getReceiver().getNome() + " " + r.getReceiver().getCognome();
            String status = r.getStatus().toString();

            ViewUserTurnRequestsDTO dto = new ViewUserTurnRequestsDTO(requestId, turnDescription, inizioEpoch, fineEpoch, userDetails, status);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    @Transactional
    public List<ViewUserTurnRequestsDTO>getRequestsToSender(@NotNull Long id){
        //TODO check if user exists
        List<Request> requests = requestTurnChangeDao.findByReceiverIdAndStatus(id, RequestENUM.PENDING);
        List<ViewUserTurnRequestsDTO> dtos = new ArrayList<>();
        for(Request r : requests){
            long requestId = r.getId();

            List<Long> list = dateAndTimeToEpoch(r.getTurn().getData(), r.getTurn().getTurno().getOraInizio(), r.getTurn().getTurno().getDurata());
            long inizioEpoch = list.get(0);
            long fineEpoch = list.get(1);

            String turnDescription = r.getTurn().getTurno().getMansione()+" in "+r.getTurn().getTurno().getServizio().getNome();
            String userDetails = r.getSender().getNome() + " " + r.getReceiver().getCognome();
            String status = r.getStatus().toString();

            ViewUserTurnRequestsDTO dto = new ViewUserTurnRequestsDTO(requestId, turnDescription, inizioEpoch, fineEpoch, userDetails, status);
            dtos.add(dto);
        }

        return dtos;
    }

}
