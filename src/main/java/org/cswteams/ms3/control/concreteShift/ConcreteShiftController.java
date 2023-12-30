package org.cswteams.ms3.control.concreteShift;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.ScheduleDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.ConcreteShiftDTO;
import org.cswteams.ms3.dto.MedicalServiceDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ConcreteShiftController implements IConcreteShiftController {
    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private ShiftDAO shiftDAO;

    /**
     * @return
     */
    @Override
    public Set<ConcreteShiftDTO> leggiTurniAssegnati() {
        Set<ConcreteShift> turniSet = new HashSet<>(concreteShiftDAO.findAll());
        Set<ConcreteShiftDTO> turniDTOSet = MappaAssegnazioneTurni.assegnazioneTurnoToDTO(turniSet);
        return turniDTOSet;
    }

    /**
     * @param dto
     * @return
     * @throws AssegnazioneTurnoException
     */
    @Override
    public ConcreteShift creaTurnoAssegnato(@NotNull RegisterConcreteShiftDTO dto) throws AssegnazioneTurnoException {

        Shift shift = shiftDAO.findAllByMedicalServicesLabelAndTimeSlot(dto.getServizio().getNome(), dto.getTimeSlot()).get(0);
        if(shift == null)
            throw new AssegnazioneTurnoException("Non esiste uno shift con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia shift: "+dto.getTimeSlot().toString());

        // TODO: Implement the correct logic this is dummy!!!
        ConcreteShift concreteShift = new ConcreteShift(LocalDate.of(dto.getYear(),dto.getMonth(),dto.getDay()).toEpochDay(), shift);

        return concreteShiftDAO.save(concreteShift);
    }

    /**
     * @param idPersona
     * @return
     */
    @Override
    public Set<ConcreteShiftDTO> leggiTurniUtente(@NotNull Long idPersona) {
        Set<ConcreteShift> turniAllocatiERiserve = concreteShiftDAO.findTurniUtente(idPersona);
        Set<ConcreteShiftDTO> turniAllocati = new HashSet<>();
        for (ConcreteShift concreteShift : turniAllocatiERiserve) {
            if(!utenteInReperibilita(concreteShift, idPersona)){
                //TODO converti entity in dto ed aggiungila a turniAllocati

                long startTime = concreteShift.getDate() + concreteShift.getShift().getStartTime().toSecondOfDay();
                long endTime =  startTime + concreteShift.getShift().getDuration().getSeconds();

                Set<UserDTO> onDutyDoctors = new HashSet<>();
                Set<UserDTO> onCallDoctors = new HashSet<>();

                for(DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()){
                    if(assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY){
                        Doctor doctor = assignment.getDoctor();
                        List<String> stringList = doctor.getSystemActors().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList());

                        onDutyDoctors.add(new UserDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getBirthday(), stringList));
                    } else if (assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL){
                        Doctor doctor = assignment.getDoctor();
                        List<String> stringList = doctor.getSystemActors().stream()
                                .map(Enum::name)
                                .collect(Collectors.toList());

                        onCallDoctors.add(new UserDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getBirthday(), stringList));
                    }
                }

                boolean onCall = !onCallDoctors.isEmpty();
                MedicalServiceDTO medicalServiceDTO = new MedicalServiceDTO(concreteShift.getShift().getMedicalService().getLabel(), concreteShift.getShift().getMedicalService().getTasks());

                ConcreteShiftDTO concreteShiftDTO = new ConcreteShiftDTO(concreteShift.getId(),
                        concreteShift.getShift().getId(), startTime, endTime, onDutyDoctors, onCallDoctors, medicalServiceDTO, concreteShift.getShift().getTimeSlot(), onCall);

            }
        }
        return turniAllocati;
    }

    private boolean utenteInReperibilita(ConcreteShift concreteShift, Long idPersona) { // ON CALL
        for(DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
            if(Objects.equals(da.getDoctor().getId(), idPersona)){
                return da.getConcreteShiftDoctorStatus().equals(ConcreteShiftDoctorStatus.ON_CALL);
            }
        }
        return false;
    }


    @Override
    public ConcreteShift leggiTurnoByID(long idAssegnazione) {
        return concreteShiftDAO.findById(idAssegnazione).get();
    }

    @Override
    @Transactional
    public ConcreteShift substituteAssignedDoctor(@NotNull ConcreteShift concreteShift, @NotNull Doctor requestingDoctor, @NotNull Doctor substituteDoctor) throws AssegnazioneTurnoException {
        if (!concreteShift.isDoctorAssigned(requestingDoctor)) {
            throw new AssegnazioneTurnoException("Doctor " + requestingDoctor + " is not on duty, nor on call for the concrete shift " + concreteShift);
        }
        if (concreteShift.getDoctorAssignmentStatus(substituteDoctor) != ConcreteShiftDoctorStatus.ON_CALL) {
            throw new AssegnazioneTurnoException("Doctor " + substituteDoctor + " is not on call for the concrete shift " + concreteShift);
        }
        int changes = 0;
        for (DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
            if (assignment.getDoctor().equals(requestingDoctor)) {
                assignment.setConcreteShiftDoctorStatus(ConcreteShiftDoctorStatus.REMOVED);
                changes++;
            } else if (assignment.getDoctor().equals(substituteDoctor)) {
                assignment.setConcreteShiftDoctorStatus(ConcreteShiftDoctorStatus.ON_DUTY);
                changes++;
            }
            if (changes == 2) break;
        }
        concreteShiftDAO.saveAndFlush(concreteShift);
        return concreteShift;
    }
}
