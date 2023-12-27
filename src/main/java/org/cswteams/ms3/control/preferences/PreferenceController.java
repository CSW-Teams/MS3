package org.cswteams.ms3.control.preferences;

import org.cswteams.ms3.dao.PreferenceDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PreferenceController implements IPreferenceController {

    @Autowired
    PreferenceDAO preferenceDao;

    @Autowired
    DoctorDAO doctorDao;

    @Override
    public Preference addPreference(PreferenceInWithUIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        if (doctor.isEmpty()){
            throw new DatabaseException("User not found");
        }

        LocalDate preferenceDay = LocalDate.of(dto.getDto().getYear(), dto.getDto().getMonth(), dto.getDto().getDay()) ;

        Preference newPreference = new Preference(preferenceDay, dto.getDto().getTurnKinds(), Collections.singletonList(doctor.get())) ;

        doctor.get().getPreferenceList().add(newPreference);
        doctorDao.save(doctor.get());
        return newPreference;
    }

    @Override
    public List<PreferenceDTOOut> addPreferences(PreferenceListWithUIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        if (doctor.isEmpty()){
            throw new DatabaseException("User not found");
        }

        ArrayList<Preference> preferencesToSave = new ArrayList<>() ;

        for (PreferenceDTOIn subDto : dto.getDto()) {
            LocalDate day = LocalDate.of(subDto.getYear(), subDto.getMonth(), subDto.getDay()) ;
            preferencesToSave.add(new Preference(day, subDto.getTurnKinds(), Collections.singletonList(doctor.get()))) ;
        }

        List<Preference> newPreferences = preferenceDao.saveAll(preferencesToSave) ;
        doctor.get().getPreferenceList().addAll(newPreferences);
        doctorDao.save(doctor.get());

        ArrayList<PreferenceDTOOut> retVal = new ArrayList<>() ;

        for (Preference pref : newPreferences) {
            retVal.add(new PreferenceDTOOut(pref.getId(), pref.getDate().getDayOfMonth(), pref.getDate().getMonthValue(), pref.getDate().getYear(), pref.getTimeSlots())) ;
        }

        return retVal;
    }

    @Override
    public void deletePreference(PreferenceDoctorIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        Optional<Preference> preference = preferenceDao.findById(dto.getPreferenceId()) ;
        if(doctor.isEmpty()){
            throw new DatabaseException("User not found");
        }
        if(preference.isEmpty()) {
            throw new DatabaseException("Preference not found") ;
        }

        doctor.get().getPreferenceList().remove(preference.get()) ;

        preferenceDao.delete(preference.get());
        doctorDao.save(doctor.get());
    }

    @Override
    public List<PreferenceDTOOut> getUsersPreferenceDTOs(DoctorIdDTO dto) {

        ArrayList<PreferenceDTOOut> retVal = new ArrayList<>();

        for (Preference pref : getUserPreferences(dto)) {
            retVal.add(new PreferenceDTOOut(pref.getId(), pref.getDate().getDayOfMonth(), pref.getDate().getMonthValue(), pref.getDate().getYear(), pref.getTimeSlots())) ;
        }
        return retVal;
    }

    @Override
    public List<Preference> getUserPreferences(DoctorIdDTO dto) {
        return preferenceDao.findAllByDoctorsId(dto.getDoctorId());
    }
}
