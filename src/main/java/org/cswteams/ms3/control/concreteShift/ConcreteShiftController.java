package org.cswteams.ms3.control.concreteShift;

import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.dto.concreteshift.GetAllConcreteShiftDTO;

import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.entity.Shift;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.exception.ConcreteShiftException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;


@Service
public class ConcreteShiftController implements IConcreteShiftController {
    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private ShiftDAO shiftDAO;

    @Override
    public Set<GetAllConcreteShiftDTO> getAllConcreteShifts() {
        List<ConcreteShift> concreteShifts = concreteShiftDAO.findAll();
        Set<ConcreteShift> turniSet = new HashSet<>();
        turniSet.addAll(concreteShifts);

        Set<GetAllConcreteShiftDTO> getAllConcreteShiftDTOSet = new HashSet<>();

        for (ConcreteShift concreteShift : turniSet) {

            long startDateTime = concreteShift.getShift().getStartTime().toEpochSecond(LocalDate.ofEpochDay(concreteShift.getDate()), ZoneOffset.UTC);
            long endDateTime = startDateTime + concreteShift.getShift().getDuration().toSeconds();

            Set<MedicalDoctorInfoDTO> doctorsOnDuty = new HashSet<>();
            Set<MedicalDoctorInfoDTO> doctorsOnCall = new HashSet<>();

            for (DoctorAssignment doctorAssignment : concreteShift.getDoctorAssignmentList()) {
                if (doctorAssignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY) {
                    Doctor doctorOnDuty = doctorAssignment.getDoctor();

                    MedicalDoctorInfoDTO medicalDoctorInfoDTO = new MedicalDoctorInfoDTO(
                            doctorOnDuty.getId(),
                            doctorOnDuty.getName(),
                            doctorOnDuty.getLastname(),
                            doctorOnDuty.getSeniority(),
                            doctorAssignment.getTask().getTaskType().toString()
                    );

                    doctorsOnDuty.add(medicalDoctorInfoDTO);
                } else if (doctorAssignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL) {
                    Doctor doctorOnCall = doctorAssignment.getDoctor();

                    MedicalDoctorInfoDTO medicalDoctorInfoDTO = new MedicalDoctorInfoDTO(
                            doctorOnCall.getId(),
                            doctorOnCall.getName(),
                            doctorOnCall.getLastname(),
                            doctorOnCall.getSeniority(),
                            doctorAssignment.getTask().getTaskType().toString()
                    );

                    doctorsOnCall.add(medicalDoctorInfoDTO);
                }
            }

            boolean isCall = !doctorsOnCall.isEmpty();

            GetAllConcreteShiftDTO getAllConcreteShiftDTO = new GetAllConcreteShiftDTO(
                    concreteShift.getId(),
                    concreteShift.getShift().getId(),
                    startDateTime,
                    endDateTime,
                    doctorsOnDuty,
                    doctorsOnCall,
                    new HashSet<>(),//TODO:inserire dottori eliminati e non un hash vuoto
                    concreteShift.getShift().getMedicalService().getLabel(),
                    "AMBULATORIO", //TODO fare
                    concreteShift.getShift().getTimeSlot().toString(),
                    isCall
            );
            getAllConcreteShiftDTOSet.add(getAllConcreteShiftDTO);
        }

        return getAllConcreteShiftDTOSet;
    }

    @Override
    public ConcreteShift createNewConcreteShift(RegisterConcreteShiftDTO dto) throws ConcreteShiftException {

        Shift shift = shiftDAO.findAllByMedicalServiceLabelAndTimeSlot(dto.getServizio().getNome(), dto.getTimeSlot()).get(0);
        if(shift == null)
            throw new ConcreteShiftException("Non esiste uno shift con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia shift: "+dto.getTimeSlot().toString());

        // TODO: Implement the correct logic this is dummy!!!
        ConcreteShift concreteShift = new ConcreteShift(LocalDate.of(dto.getYear(),dto.getMonth(),dto.getDay()).toEpochDay(), shift);

        return concreteShiftDAO.save(concreteShift);
    }

    @Override
    public Set<GetAllConcreteShiftDTO> getSingleDoctorConcreteShifts(Long idPersona) {
        List<ConcreteShift> turniAllocatiERiserve = concreteShiftDAO.findByDoctorAssignmentList_Doctor_Id(idPersona);
        Set<GetAllConcreteShiftDTO> getAllConcreteShiftDTOSet = new HashSet<>();
        for (ConcreteShift concreteShift : turniAllocatiERiserve) {
            if(!isDoctorOnCall(concreteShift, idPersona)){
                //TODO converti entity in dto ed aggiungila a turniAllocati

                // the Epoch Day gets converted to Epoch Second
                long startTime = concreteShift.getDate()*24*60*60 + concreteShift.getShift().getStartTime().toSecondOfDay();
                long endTime =  startTime + concreteShift.getShift().getDuration().getSeconds();

                Set<MedicalDoctorInfoDTO> onDutyDoctors = new HashSet<>();
                Set<MedicalDoctorInfoDTO> onCallDoctors = new HashSet<>();
                Set<MedicalDoctorInfoDTO> onRemovedDoctors = new HashSet<>();
                for(DoctorAssignment assignment : concreteShift.getDoctorAssignmentList()) {
                    if (assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_DUTY) {
                        Doctor doctor = assignment.getDoctor();
                        onDutyDoctors.add(new MedicalDoctorInfoDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getSeniority(), assignment.getTask().getTaskType().toString()));
                    } else if (assignment.getConcreteShiftDoctorStatus() == ConcreteShiftDoctorStatus.ON_CALL) {
                        Doctor doctor = assignment.getDoctor();
                        onCallDoctors.add(new MedicalDoctorInfoDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getSeniority(), assignment.getTask().getTaskType().toString()));
                    } else{
                        Doctor doctor = assignment.getDoctor();
                        onRemovedDoctors.add(new MedicalDoctorInfoDTO(doctor.getId(), doctor.getName(), doctor.getLastname(), doctor.getSeniority(), assignment.getTask().getTaskType().toString()));
                    }
                }

                boolean isCall = !onCallDoctors.isEmpty();

                GetAllConcreteShiftDTO getAllConcreteShiftDTO = new GetAllConcreteShiftDTO(
                        concreteShift.getId(),
                        concreteShift.getShift().getId(),
                        startTime,
                        endTime,
                        onDutyDoctors,
                        onCallDoctors,
                        onRemovedDoctors,
                        concreteShift.getShift().getMedicalService().getLabel(),
                        "AMBULATORIO",//TODO: va RIVISTA ASSOLUTAMENTE
                        concreteShift.getShift().getTimeSlot().toString(),
                        isCall
                );
                getAllConcreteShiftDTOSet.add(getAllConcreteShiftDTO);
            }
        }
        return getAllConcreteShiftDTOSet;
    }

    /**
     * Check if a <i>Doctor</i> is on call for <code>concreteShift</code>
     *
     * @param concreteShift concrete shift
     * @param doctorId     doctor id
     * @return <code>true</code> if the <i>doctor</i> identified by <code>doctorId</code> is on call for <code>concreteShift</code>,
     * <code>false</code> elsewhere.
     */
    private boolean isDoctorOnCall(ConcreteShift concreteShift, Long doctorId) { // ON CALL
        for(DoctorAssignment da : concreteShift.getDoctorAssignmentList()){
            if(Objects.equals(da.getDoctor().getId(), doctorId)){
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
    public ConcreteShift substituteAssignedDoctor(ConcreteShift concreteShift, Doctor requestingDoctor, Doctor substituteDoctor) throws ConcreteShiftException {
        if (!concreteShift.isDoctorAssigned(requestingDoctor)) {
            throw new ConcreteShiftException("Doctor " + requestingDoctor + " is not on duty, nor on call for the concrete shift " + concreteShift);
        }
        if (concreteShift.getDoctorAssignmentStatus(substituteDoctor) != ConcreteShiftDoctorStatus.ON_CALL) {
            throw new ConcreteShiftException("Doctor " + substituteDoctor + " is not on call for the concrete shift " + concreteShift);
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