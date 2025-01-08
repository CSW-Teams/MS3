package org.cswteams.ms3.control.preferences;

import org.cswteams.ms3.dao.PreferenceDAO;
import org.cswteams.ms3.dao.DoctorDAO;
import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.enums.TimeSlot;
import org.cswteams.ms3.exception.DatabaseException;
import org.cswteams.ms3.jpa_constraints.validant.Validant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Service
public class PreferenceController implements IPreferenceController {

    @Autowired
    PreferenceDAO preferenceDao;

    @Autowired
    DoctorDAO doctorDao;

    private PreferenceDTOOut convertPreferenceToDTO(Preference pref) {
        Set<String> timeSlotStrings = new HashSet<>() ;
        for (TimeSlot t : pref.getTimeSlots()) {
            timeSlotStrings.add(t.name()) ;
        }

        return new PreferenceDTOOut(pref.getId(), pref.getDate().getDayOfMonth(), pref.getDate().getMonthValue(), pref.getDate().getYear(), timeSlotStrings) ;
    }

    @Override
    @Validant
    @Transactional
    public Preference addPreference(@Valid PreferenceInWithUIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        if (doctor.isEmpty()){
            throw new DatabaseException("TenantUser not found");
        }

        LocalDate preferenceDay = LocalDate.of(dto.getDto().getYear(), dto.getDto().getMonth(), dto.getDto().getDay()) ;

        Set<TimeSlot> slots = new HashSet<>() ;

        for (String s : dto.getDto().getTurnKinds()) {
            slots.add(TimeSlot.valueOf(s)) ;
        }

        Preference newPreference ;
        if(dto.getDto().getId() == null) {
            newPreference = new Preference(preferenceDay, slots, Collections.singletonList(doctor.get())) ;
        } else {
            newPreference = new Preference(dto.getDto().getId(), preferenceDay, slots, Collections.singletonList(doctor.get())) ;
        }

        if(!doctor.get().getPreferenceList().contains(newPreference))
            doctor.get().getPreferenceList().add(newPreference);
        doctorDao.save(doctor.get());
        return newPreference;
    }

    @Override
    @Validant
    @Transactional
    public List<PreferenceDTOOut> addPreferences(@Valid PreferenceListWithUIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        if (doctor.isEmpty()){
            throw new DatabaseException("TenantUser not found");
        }

        ArrayList<Preference> preferencesToSave = new ArrayList<>() ;

        for (PreferenceDTOIn subDto : dto.getDto()) {
            LocalDate day = LocalDate.of(subDto.getYear(), subDto.getMonth(), subDto.getDay()) ;

            Set<TimeSlot> slots = new HashSet<>() ;

            for (String s : subDto.getTurnKinds()) {
                slots.add(TimeSlot.valueOf(s)) ;
            }

            Preference preferenceToSave ;
            if(subDto.getId() != null) {
                preferenceToSave = new Preference(subDto.getId(), day, slots, Collections.singletonList(doctor.get())) ;
            } else {
                preferenceToSave = new Preference(day, slots, Collections.singletonList(doctor.get())) ;
            }
            preferencesToSave.add(preferenceToSave) ;
        }

        List<Preference> newPreferences = preferenceDao.saveAll(preferencesToSave) ;

        for (Preference pref : newPreferences) {
            if(!doctor.get().getPreferenceList().contains(pref))
                doctor.get().getPreferenceList().add(pref);
        }

        doctorDao.save(doctor.get());

        ArrayList<PreferenceDTOOut> retVal = new ArrayList<>() ;

        for (Preference pref : newPreferences) {

            retVal.add(convertPreferenceToDTO(pref)) ;
        }

        return retVal;
    }

    @Override
    @Validant
    @Transactional
    public void deletePreference(@Valid PreferenceDoctorIDDTO dto) throws DatabaseException {
        Optional<Doctor> doctor = doctorDao.findById(dto.getDoctorId());
        Optional<Preference> preference = preferenceDao.findById(dto.getPreferenceId()) ;
        if(doctor.isEmpty()){
            throw new DatabaseException("TenantUser not found");
        }
        if(preference.isEmpty()) {
            throw new DatabaseException("Preference not found") ;
        }

        doctor.get().getPreferenceList().remove(preference.get()) ;

        preferenceDao.delete(preference.get());
        doctorDao.save(doctor.get());
    }

    @Override
    @Validant
    public List<PreferenceDTOOut> getUsersPreferenceDTOs(@Valid DoctorIdDTO dto) {

        ArrayList<PreferenceDTOOut> retVal = new ArrayList<>();

        for (Preference pref : getUserPreferences(dto)) {

            retVal.add(convertPreferenceToDTO(pref)) ;
        }
        return retVal;
    }

    @Override
    @Validant
    @Transactional
    public List<PreferenceDTOOut> editPreferences(@Valid EditedPreferencesDTOIn dto) throws DatabaseException {
        for (PreferenceDoctorIDDTO doctorIdDTO : dto.getPreferencesToDelete()) {
            deletePreference(doctorIdDTO) ;
        }
        return addPreferences(new PreferenceListWithUIDDTO(dto.getDoctorId(), dto.getRemainingPreferences())) ;
    }

    @Override
    @Validant
    public List<Preference> getUserPreferences(@Valid DoctorIdDTO dto) {
        return preferenceDao.findAllByDoctorsId(dto.getDoctorId());
    }
}
