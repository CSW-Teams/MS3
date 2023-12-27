package org.cswteams.ms3.control.preferences;

import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.exception.DatabaseException;

import java.util.List;

public interface IPreferenceController {

    Preference addPreference(PreferenceInWithUIDDTO dto) throws DatabaseException;

    List<PreferenceDTOOut> addPreferences(PreferenceListWithUIDDTO dto) throws DatabaseException;

    void deletePreference(PreferenceDoctorIDDTO dto) throws DatabaseException;

    List<PreferenceDTOOut> getUsersPreferenceDTOs(DoctorIdDTO dto);

    List<Preference> getUserPreferences(DoctorIdDTO dto);
}
