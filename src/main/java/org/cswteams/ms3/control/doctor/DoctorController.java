package org.cswteams.ms3.control.doctor;

import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dao.PermanentConditionDAO;
import org.cswteams.ms3.dao.SpecializationDAO;
import org.cswteams.ms3.dao.TemporaryConditionDAO;
import org.cswteams.ms3.dto.condition.UpdateConditionsDTO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Specialization;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.cswteams.ms3.entity.condition.TemporaryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorController implements IDoctorController {

    @Autowired
    private DoctorDAO doctorDAO;
    @Autowired
    private SpecializationDAO specializationDAO;
    @Autowired
    private PermanentConditionDAO permanentConditionDAO;
    @Autowired
    private TemporaryConditionDAO temporaryConditionDAO;

    @Override
    public Set<MedicalDoctorInfoDTO> getAllDoctors() {

        List<Doctor> doctors = doctorDAO.findAll();
        Set<MedicalDoctorInfoDTO> doctorsSet = new HashSet<>();

        for (Doctor d: doctors){

            MedicalDoctorInfoDTO dto = new MedicalDoctorInfoDTO(
                    d.getId(),
                    d.getName(),
                    d.getLastname(),
                    d.getSeniority(),
                    ""
            );
            doctorsSet.add(dto);
        }

        return doctorsSet;
    }

    @Override
    public MedicalDoctorInfoDTO getDoctorById(Long id) {
        Optional<Doctor> doctor = doctorDAO.findById(id);
        return doctor.map(value -> new MedicalDoctorInfoDTO(value.getId(), value.getName(), value.getLastname(), value.getSeniority(), "")).orElse(null);
    }

    // TODO: Add checks on values
    @Override
    public void deleteDoctorSpecialization(Long doctorID, String specialization) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        Specialization dbSpecialization = specializationDAO.findByType(specialization);
        doctor.getSpecializations().remove(dbSpecialization);
        doctorDAO.saveAndFlush(doctor);
    }

    // TODO: Add checks on values
    @Override
    public void addDoctorSpecialization(Long doctorID, Set<String> specialization) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        for(String stringSpecialization: specialization){
            Specialization dbSpecialization = specializationDAO.findByType(stringSpecialization);
            if(!doctor.getSpecializations().contains(dbSpecialization))
                doctor.getSpecializations().add(dbSpecialization);
        }
        doctorDAO.saveAndFlush(doctor);
    }

    @Override
    public void deleteDoctorPermanentCondition(Long doctorID, Long conditionID, String condition) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        PermanentCondition dbCondition = permanentConditionDAO.findById(conditionID);
        doctor.getPermanentConditions().remove(dbCondition);
        doctorDAO.saveAndFlush(doctor);
    }

    @Override
    public void deleteDoctorTemporaryCondition(Long doctorID, Long conditionID, String condition) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        TemporaryCondition dbCondition = temporaryConditionDAO.findById(conditionID);
        doctor.getTemporaryConditions().remove(dbCondition);
        doctorDAO.saveAndFlush(doctor);
    }

    @Override
    public long addDoctorCondition(Long doctorID, UpdateConditionsDTO.GenericCondition condition) {
        Doctor doctor = doctorDAO.findById((long) doctorID);
        long conditionID = -1;

        if(condition.getStartDate() == 0){
            /* This is a permanent condition*/
            PermanentCondition exists = permanentConditionDAO.findByType(condition.getCondition());
            if(exists != null){
                conditionID = exists.getId();
                doctor.getPermanentConditions().add(exists);
            }
        }else{
            /* This is a temporary condition */
            TemporaryCondition exists = temporaryConditionDAO.findByType(condition.getCondition());

            if(exists != null){
                TemporaryCondition temp = new TemporaryCondition(condition.getCondition(), condition.getStartDate(), condition.getEndDate());
                temporaryConditionDAO.saveAndFlush(temp);
                conditionID = temp.getId();
                doctor.getTemporaryConditions().add(temp);
            }
        }
        doctorDAO.saveAndFlush(doctor);

        return conditionID;
    }
}
