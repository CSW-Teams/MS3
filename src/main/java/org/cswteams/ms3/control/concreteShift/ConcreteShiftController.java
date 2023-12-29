package org.cswteams.ms3.control.concreteShift;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.dao.ConcreteShiftDAO;
import org.cswteams.ms3.dao.ShiftDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.ConcreteShiftDTO;
import org.cswteams.ms3.dto.RegisterConcreteShiftDTO;
import org.cswteams.ms3.entity.ConcreteShift;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.DoctorAssignment;
import org.cswteams.ms3.enums.ConcreteShiftDoctorStatus;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;


@Service
public class ConcreteShiftController implements IConcreteShiftController {
    @Autowired
    private ConcreteShiftDAO concreteShiftDAO;

    @Autowired
    private DoctorDAO doctorDao;

    @Autowired
    private ShiftDAO shiftDAO;

    /*@Autowired
    private ScheduleDAO scheduleDao;
*/

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

        //Shift shift = shiftDAO.findAllByMedicalServicesLabelAndTimeSlot(dto.getServizio().getNome(), dto.getTimeSlot()).get(0);
        //if(shift == null)
        //    throw new AssegnazioneTurnoException("Non esiste un shift con la coppia di attributi servizio: "+dto.getServizio().getNome() +",tipologia shift: "+dto.getTimeSlot().toString());

        // TODO: Implement the correct logic this is dummy!!!
        //ConcreteShift concreteShift = new ConcreteShift(LocalDate.of(dto.getAnno(),dto.getMese(),dto.getGiorno()).toEpochDay(), shift, new HashMap<>());

        //return assegnazioneTurnoDao.save(concreteShift);
        return null;
    }

    /**
     * @param idPersona
     * @return
     * @throws ParseException
     */
    @Override
    public Set<ConcreteShiftDTO> leggiTurniUtente(@NotNull Long idPersona) throws ParseException {
        Set<ConcreteShift> turniAllocatiERiserve = concreteShiftDAO.findTurniUtente(idPersona);
        Set<ConcreteShiftDTO> turniAllocati = new HashSet<>();
        for (ConcreteShift concreteShift : turniAllocatiERiserve) {
            //if(!utenteInReperibilita(concreteShift, idPersona))
            //turniAllocati.add(MappaAssegnazioneTurni.assegnazioneTurnoToDTO(concreteShift));
        }
        return turniAllocati;
    }

    private boolean utenteInReperibilita(ConcreteShift concreteShift, Long idPersona) {
        /*for(Doctor doctorReperibile : concreteShift.getDoctorsOnCall()){
            if(doctorReperibile.getId().longValue() == idPersona.longValue())
                return true;
        }*/
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
