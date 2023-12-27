package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.preferences.IPreferenceController;
import org.cswteams.ms3.dto.preferences.*;
import org.cswteams.ms3.exception.DatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferences/")
public class PreferencesRestEndpoint {

    @Autowired
    IPreferenceController preferenceController;

    @RequestMapping(method = RequestMethod.GET, path = "/doctor_id={doctorId}")
    public ResponseEntity<?> readUserPreferences(@PathVariable Long doctorId){
        if (doctorId != null) {
            List<PreferenceDTOOut> c = preferenceController.getUsersPreferenceDTOs(new DoctorIdDTO(doctorId));
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/preference_id={preferenceId}/doctor_id={doctorId}")
    public ResponseEntity<?> deleteUserPreference(@PathVariable Long preferenceId, @PathVariable Long doctorId){
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

    @RequestMapping(method = RequestMethod.POST, path = "/doctor_id={doctorId}")
    public ResponseEntity<?> addPreferences(@RequestBody() List<PreferenceDTOIn> preferenceDTOInList, @PathVariable Long doctorId) throws Exception {
        if (preferenceDTOInList != null) {
            PreferenceListWithUIDDTO dto = new PreferenceListWithUIDDTO(doctorId, preferenceDTOInList) ;
            return new ResponseEntity<>(preferenceController.addPreferences(dto), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
