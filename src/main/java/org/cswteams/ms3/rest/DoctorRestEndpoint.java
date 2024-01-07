package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.doctor.IDoctorController;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/doctors/")
public class DoctorRestEndpoint {

    @Autowired
    private IDoctorController doctorController;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllDoctors() {
        Set<MedicalDoctorInfoDTO> medicalDoctorInfoDTOSet = doctorController.getAllDoctors();
        return new ResponseEntity<>(medicalDoctorInfoDTOSet, HttpStatus.FOUND);
    }
}
