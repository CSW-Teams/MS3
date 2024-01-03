package org.cswteams.ms3.control.concreteShift;

import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dto.medicalservice.MedicalServiceDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.concreteshift.GetAllConcreteShiftDTO;
import org.cswteams.ms3.dto.user.UserDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
    public Set<GetAllConcreteShiftDTO> getAllConcreteShifts() {
        List<ConcreteShift> concreteShifts = concreteShiftDAO.findAll();
        Set<ConcreteShift> turniSet = new HashSet<>();
        turniSet.addAll(concreteShifts);

        Set<GetAllConcreteShiftDTO> getAllConcreteShiftDTOSet = new HashSet<>();

        for (ConcreteShift concreteShift : turniSet) {

            long startDateTime = concreteShift.getShift().getStartTime().toEpochSecond(LocalDate.ofEpochDay(concreteShift.getDate()), ZoneOffset.UTC);
            long endDateTime = startDateTime + concreteShift.getShift().getDuration().toSeconds();

            Set<UserDTO> doctorsOnDuty = new HashSet<>();
            Set<UserDTO> doctorsOnCall = new HashSet<>();

            for (DoctorAssignment doctorAssignment : concreteShift.getDoctorAssignmentList()) {
                if (doctorAssignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY) {
                    Doctor doctorOnDuty = doctorAssignment.getDoctor();
                    /* todo can't we just pass a doctor's list to GetAllConcreteShiftDTO? */

                    /* this is because UserDTO wants a list of strings for systemActors */
                    List<String> systemActors = new ArrayList<>();
                    for (SystemActor actor : doctorOnDuty.getSystemActors()) {
                        systemActors.add(actor.toString());
                    }

                    UserDTO userDTO = new UserDTO(
                            doctorOnDuty.getId(),
                            doctorOnDuty.getName(),
                            doctorOnDuty.getLastname(),
                            doctorOnDuty.getBirthday(),
                            systemActors
                    );

                    doctorsOnDuty.add(userDTO);
                } else if (doctorAssignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL) {
                    Doctor doctorOnCall = doctorAssignment.getDoctor();

                    /* this is because UserDTO wants a list of strings for systemActors */
                    List<String> systemActors = new ArrayList<>();
                    for (SystemActor actor : doctorOnCall.getSystemActors()) {
                        systemActors.add(actor.toString());
                    }

                    UserDTO userDTO = new UserDTO(
                            doctorOnCall.getId(),
                            doctorOnCall.getName(),
                            doctorOnCall.getLastname(),
                            doctorOnCall.getBirthday(),
                            systemActors
                    );

                    doctorsOnCall.add(userDTO);
                }
            }

            GetAllConcreteShiftDTO getAllConcreteShiftDTO = new GetAllConcreteShiftDTO(
                    concreteShift.getId(),
                    concreteShift.getShift().getId(),
                    startDateTime,
                    endDateTime,
                    doctorsOnDuty,
                    doctorsOnCall,
                    concreteShift.getShift().getMedicalService().getLabel(),
                    "AMBULATORIO",  // TODO: Change medical service List of tasks
                    concreteShift.getShift().getTimeSlot().toString(),
                    true
            );
            getAllConcreteShiftDTOSet.add(getAllConcreteShiftDTO);
        }

        return getAllConcreteShiftDTOSet;
    }

    /**
     * @param dto
     * @return
     * @throws AssegnazioneTurnoException
     */
    @Override
    public ConcreteShift createNewConcreteShift(RegisterConcreteShiftDTO dto) throws AssegnazioneTurnoException {

        Shift shift = shiftDAO.findAllByMedicalServiceLabelAndTimeSlot(dto.getServizio().getNome(), dto.getTimeSlot()).get(0);
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
    public Set<GetAllConcreteShiftDTO> getSingleDoctorConcreteShifts(Long idPersona) {
        Set<ConcreteShift> turniAllocatiERiserve = concreteShiftDAO.findByDoctorAssignmentList_Doctor_Id(idPersona);
        Set<GetAllConcreteShiftDTO> getAllConcreteShiftDTOSet = new HashSet<>();


        for (ConcreteShift concreteShift : turniAllocatiERiserve) {
            if(!utenteInReperibilita(concreteShift, idPersona)){
                //TODO converti entity in dto ed aggiungila a turniAllocati

                // the Epoch Day gets converted to Epoch Second
                long startTime = concreteShift.getDate()*24*60*60 + concreteShift.getShift().getStartTime().toSecondOfDay();
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

                GetAllConcreteShiftDTO getAllConcreteShiftDTO = new GetAllConcreteShiftDTO(
                        concreteShift.getId(),
                        concreteShift.getShift().getId(),
                        startTime,
                        endTime,
                        concreteShift.getShift().getMedicalService().getLabel(),
                        "AMBULATORIO",  // TODO: Chenga medical service List of taks
                        concreteShift.getShift().getTimeSlot().toString(),
                        true
                );
                getAllConcreteShiftDTOSet.add(getAllConcreteShiftDTO);
            }
        }
        return getAllConcreteShiftDTOSet;
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
    public ConcreteShift getConcreteShiftById(long idAssegnazione) {
        return concreteShiftDAO.findById(idAssegnazione).get();
    }

    @Override
    @Transactional
    public ConcreteShift substituteAssignedDoctor(ConcreteShift concreteShift, Doctor requestingDoctor, Doctor substituteDoctor) throws AssegnazioneTurnoException {
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
