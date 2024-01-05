package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.preferences.IPreferenceController;
import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ValidationException;
import java.util.List;

/**
 * REST endpoint for api/preferences
 */
@RestController
@RequestMapping("/preferences/")
public class PreferencesRestEndpoint {

    @Autowired
    IPreferenceController preferenceController;

    /**
     * Retrieves a doctor's preferences <br/>
     * Reached from <b>GET api/preferences/doctor_id={doctorId}</b>
     * @param doctorId The id of the interested doctor
     * @return the doctor's preferences as {@link org.cswteams.ms3.dto.preferences.PreferenceDTOOut} in the response body
     */
    @RequestMapping(method = RequestMethod.GET, path = "/doctor_id={doctorId}")
    public ResponseEntity<?> readDoctorPreferences(@PathVariable Long doctorId){
        if (doctorId != null) {
            List<PreferenceDTOOut> c = preferenceController.getUsersPreferenceDTOs(new DoctorIdDTO(doctorId));
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Deletes a doctor's preference <br/>
     * Reached from <b>DELETE api/preferences/preference_id={preferenceId}/doctor_id={doctorId}</b>
     * @param preferenceId the id of the preference to delete
     * @param doctorId the id of the doctor to delete
     * @return A positive response in case of success, a negative one otherwise
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/preference_id={preferenceId}/doctor_id={doctorId}")
    public ResponseEntity<?> deleteDoctorPreference(@PathVariable Long preferenceId, @PathVariable Long doctorId){
        if (preferenceId != null && doctorId != null) {
            try {
                preferenceController.deletePreference(new PreferenceDoctorIDDTO(doctorId, preferenceId));
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Adds preferences to a doctor <br/>
     * Reached from <b>POST api/preferences/doctor_id={doctorId}</b>
     * @param preferenceDTOInList A List of DTOs containing the preferences
     * @param doctorId the id representing the doctor to whom the preferences shall be added
     * @return A List of {@link org.cswteams.ms3.dto.preferences.PreferenceDTOOut} of the newly added preferences, with their own id, in the body of the response
     */
    @RequestMapping(method = RequestMethod.POST, path = "/doctor_id={doctorId}")
    public ResponseEntity<?> addPreferences(@RequestBody() List<PreferenceDTOIn> preferenceDTOInList, @PathVariable Long doctorId) {
        if (preferenceDTOInList != null) {
            try {
                PreferenceListWithUIDDTO dto = new PreferenceListWithUIDDTO(doctorId, preferenceDTOInList) ;
                return new ResponseEntity<>(preferenceController.addPreferences(dto), HttpStatus.ACCEPTED);
            } catch (ValidationException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE) ;
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST) ;
            }

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Edits the preferences of a doctor, eventually adding new ones and deleting unwanted ones <br/>
     * Reached from <b>POST api/preferences/edit</b>
     * @return A response containing the list of {@link org.cswteams.ms3.dto.preferences.PreferenceDTOOut} representing the edited (and remaining) preferences,
     *             giving the newly added ones their own id, too
     */
    @RequestMapping(method = RequestMethod.POST, path = "/edit")
    public ResponseEntity<?> editPreferences(@RequestBody() EditedPreferencesDTOIn dto) {
        if(dto != null) {
            try {
                return new ResponseEntity<>(preferenceController.editPreferences(dto), HttpStatus.ACCEPTED);
            } catch (ValidationException e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE) ;
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST) ;
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
