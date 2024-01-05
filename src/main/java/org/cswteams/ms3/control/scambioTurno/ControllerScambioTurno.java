package org.cswteams.ms3.control.scambioTurno;

import lombok.SneakyThrows;
import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.DoctorAssignmentDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.ShiftChangeRequestDAO;
import org.cswteams.ms3.dto.AnswerTurnChangeRequestDTO;
import org.cswteams.ms3.dto.RequestTurnChangeDto;
import org.cswteams.ms3.dto.ViewUserTurnRequestsDTO;
import org.cswteams.ms3.entity.*;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.RequestStatus;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.cswteams.ms3.exception.ShiftException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ControllerScambioTurno implements IControllerScambioTurno {

    @Autowired
    private DoctorDAO utenteDao;

    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private ShiftChangeRequestDAO shiftChangeRequestDAO;

    @Autowired
    private DoctorAssignmentDAO doctorAssignmentDAO;

    /**
     * Questo metodo crea una richiesta di modifica turno.
     * @param requestTurnChangeDto
     * @return
     */
    @Override
    @Transactional
    public void requestTurnChange(@NotNull RequestTurnChangeDto requestTurnChangeDto) throws AssegnazioneTurnoException {
        Optional<ConcreteShift> assegnazioneTurno = concreteShiftDAO.findById(requestTurnChangeDto.getConcreteShiftId());
        if(assegnazioneTurno.isEmpty()){
            throw new AssegnazioneTurnoException("Turno non presente");
        }

        Optional<Doctor> senderOptional = Optional.ofNullable(utenteDao.findById(requestTurnChangeDto.getSenderId()));
        if(senderOptional.isEmpty()){
            throw new AssegnazioneTurnoException("Utente richiedente non presente nel database");
        }


        Optional<Doctor> receiverOptional = Optional.ofNullable(utenteDao.findById(requestTurnChangeDto.getReceiverId()));
        if(receiverOptional.isEmpty()){
            throw new AssegnazioneTurnoException("Utente richiesto non presente nel database");
        }

        ConcreteShift concreteShift = assegnazioneTurno.get();

        List<Long> onDutyDoctorIds = new ArrayList<>();
        List<Long> onCallDoctorIds = new ArrayList<>();

        for(DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()){
            if(assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY){
                onDutyDoctorIds.add(assignment.getDoctor().getId());
            } else if (assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL){
                onCallDoctorIds.add(assignment.getDoctor().getId());
            }
        }

        if(!onCallDoctorIds.contains(requestTurnChangeDto.getSenderId()) && !onDutyDoctorIds.contains(requestTurnChangeDto.getSenderId())){
            throw new AssegnazioneTurnoException("Utente richiedente non assegnato al turno");
        }

        if(onDutyDoctorIds.contains(requestTurnChangeDto.getReceiverId()) || onCallDoctorIds.contains(requestTurnChangeDto.getReceiverId())){
            throw new AssegnazioneTurnoException("Utente richiesto già assegnato al turno");
        }



        Request request = new Request(senderOptional.get(), receiverOptional.get(), concreteShift);

        List<Request> requests = shiftChangeRequestDAO.findBySenderIdAndTurnIdAndStatus(requestTurnChangeDto.getSenderId(), requestTurnChangeDto.getConcreteShiftId(), RequestStatus.PENDING);

        if(!requests.isEmpty()){
            throw new AssegnazioneTurnoException("esiste già una richiesta in corso per la modifica di questo turno");
        }

        try {
            shiftChangeRequestDAO.saveAndFlush(request);
        } catch(ConstraintViolationException e){
            throw new AssegnazioneTurnoException("esiste già un cambio pendente");
        }
    }

    private List<Long> dateAndTimeToEpoch(long startDate, LocalTime startTime, Duration duration){
        LocalDate localStartDate = LocalDate.ofEpochDay(startDate);
        long startDateInSeconds = localStartDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long startLocalTime = startTime.toSecondOfDay();
        long newStartTime = startDateInSeconds + startLocalTime;
        long endTime = newStartTime + duration.getSeconds();

        List<Long> l = new ArrayList<>();
        l.add(newStartTime);
        l.add(endTime);

        return l;
    }

    @Override
    @Transactional
    public List<ViewUserTurnRequestsDTO> getRequestsBySender(@NotNull Long id){
        //TODO check if user exists
        List<Request> requests = shiftChangeRequestDAO.findBySenderId(id);
        List<ViewUserTurnRequestsDTO> dtos = new ArrayList<>();
        for(Request r : requests){
            long requestId = r.getId();

            List<Long> list = dateAndTimeToEpoch(r.getTurn().getDate(), r.getTurn().getShift().getStartTime(), r.getTurn().getShift().getDuration());
            long inizioEpoch = list.get(0);
            long fineEpoch = list.get(1);

            DoctorAssignment doctorAssignment = null;
            for(DoctorAssignment da : r.getTurn().getDoctorAssignmentList()){
                if(Objects.equals(da.getDoctor().getId(), r.getSender().getId())){
                    doctorAssignment = da;
                    break;
                }
            }

            if(doctorAssignment == null){
                return new ArrayList<>();
            }

            String turnDescription = doctorAssignment.getTask().getTaskType() +" come "+doctorAssignment.getConcreteShiftDoctorStatus();
            String userDetails = r.getReceiver().getName() + " " + r.getReceiver().getLastname();
            String status = r.getStatus().toString();

            ViewUserTurnRequestsDTO dto = new ViewUserTurnRequestsDTO(requestId, turnDescription, inizioEpoch, fineEpoch, userDetails, status);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    @Transactional
    public List<ViewUserTurnRequestsDTO> getRequestsToSender(@NotNull Long id){
        //TODO check if user exists
        List<Request> requests = shiftChangeRequestDAO.findByReceiverIdAndStatus(id, RequestStatus.PENDING);
        List<ViewUserTurnRequestsDTO> dtos = new ArrayList<>();
        for(Request r : requests){
            long requestId = r.getId();

            List<Long> list = dateAndTimeToEpoch(r.getTurn().getDate(), r.getTurn().getShift().getStartTime(), r.getTurn().getShift().getDuration());
            long inizioEpoch = list.get(0);
            long fineEpoch = list.get(1);

            DoctorAssignment doctorAssignment = null;
            for(DoctorAssignment da : r.getTurn().getDoctorAssignmentList()){
                if(Objects.equals(da.getDoctor().getId(), r.getSender().getId())){
                    doctorAssignment = da;
                    break;
                }
            }

            if(doctorAssignment == null){
                return new ArrayList<>();
            }

            String turnDescription = doctorAssignment.getTask().getTaskType() +" come "+doctorAssignment.getConcreteShiftDoctorStatus();

            String userDetails = r.getSender().getName() + " " + r.getSender().getLastname();
            String status = r.getStatus().toString();

            ViewUserTurnRequestsDTO dto = new ViewUserTurnRequestsDTO(requestId, turnDescription, inizioEpoch, fineEpoch, userDetails, status);
            dtos.add(dto);
        }

        return dtos;
    }

    @Transactional
    @Override
    public void answerTurnChangeRequest(AnswerTurnChangeRequestDTO answerTurnChangeRequestDTO) throws ShiftException {
        Optional<Request> optionalRequest=shiftChangeRequestDAO.findById(answerTurnChangeRequestDTO.getRequestID());
        if (optionalRequest.isEmpty()){
                throw new ShiftException("Utente Richiesto non trovato");
        }
        Request request=optionalRequest.get();

        if(answerTurnChangeRequestDTO.isHasAccepted()){
            request.setStatus(RequestStatus.ACCEPTED);
            ConcreteShift shift = request.getTurn();
            Doctor newDoctor=utenteDao.getOne(request.getReceiver().getId());
            List<DoctorAssignment> list=shift.getDoctorAssignmentList();
            for(int i=0;i<list.size();i++){
                DoctorAssignment currAssignment = list.get(i);
                long currId = currAssignment.getDoctor().getId();
                if(currId == request.getSender().getId()){
                    DoctorAssignment newDoctorAssignment = new DoctorAssignment(newDoctor,
                            currAssignment.getConcreteShiftDoctorStatus(),
                            currAssignment.getConcreteShift(),
                            currAssignment.getTask());
                    newDoctorAssignment=doctorAssignmentDAO.saveAndFlush(newDoctorAssignment);
                    list.add(newDoctorAssignment);
                    currAssignment.setConcreteShiftDoctorStatus(ConcreteShiftDoctorStatus.REMOVED);
                    doctorAssignmentDAO.save(currAssignment);
                    concreteShiftDAO.save(shift);
                    break;
                }
            }
        }else{
                request.setStatus(RequestStatus.REFUSED);
                //TODO: sarebbe da notificare l'altro utente del rifiuto
                shiftChangeRequestDAO.saveAndFlush(request);
        }
    }
}
