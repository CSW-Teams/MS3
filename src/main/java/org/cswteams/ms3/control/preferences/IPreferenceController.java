package org.cswteams.ms3.control.preferences;

import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.entity.Preference;
import org.cswteams.ms3.exception.DatabaseException;

import java.util.List;

/**
 * This interface is responsible for managing at business level the scheduling preferences of doctors
 */
public interface IPreferenceController {

    /**
     * Adds a shceduling preference to a doctor
     * @param dto DTO containing all the info about the preference and the doctor id
     * @return a Preference entity with a JPA-assigned id
     * @throws DatabaseException upon save errors
     */
    Preference addPreference(PreferenceInWithUIDDTO dto) throws DatabaseException;

    /**
     * Adds a set of preferences to a doctor
     * @param dto DTO containing a list with all the info about the preferences to be added and the doctor id
     * @return a list of PreferenceDTOOut with JPA-assigned ids
     * @throws DatabaseException upon save errors
     */
    List<PreferenceDTOOut> addPreferences(PreferenceListWithUIDDTO dto) throws DatabaseException;

    /**
     * Deletes a preference of a doctor
     * @param dto DTO containing the ids of the preference to be deleted from the doctor and the id of the doctor
     * @throws DatabaseException upon deletion errors
     */
    void deletePreference(PreferenceDoctorIDDTO dto) throws DatabaseException;

    /**
     * Retrieves a doctor's scheduling preferences in DTO form
     * @param dto A DTO containing the doctor's id for the search
     * @return A list of PreferenceDTOOut representing the doctor's scheduling preferences
     */
    List<PreferenceDTOOut> getUsersPreferenceDTOs(DoctorIdDTO dto);

    /**
     * Edits the scheduling preferences of a doctor, eventually creating new ones and removing the not anymore needed ones
     * @param dto a DTO containing all the edited preferences and the references to the preferences to delete. The newly added preferences are marked by their lack of an id
     * @return A list of {@link org.cswteams.ms3.dto.preferences.PreferenceDTOOut} representing the newly updated preferences (gives to the receiver the ids of the newly defined ones, too
     * @throws DatabaseException upon deletion errors
     */
    List<PreferenceDTOOut> editPreferences(EditedPreferencesDTOIn dto) throws DatabaseException ;

    /**
     * Retrieves a user's scheduling preferences
     * @param dto A DTO containing the doctor's id for the search
     * @return A list of doctor's scheduling preferences
     */
    List<Preference> getUserPreferences(DoctorIdDTO dto);
}
