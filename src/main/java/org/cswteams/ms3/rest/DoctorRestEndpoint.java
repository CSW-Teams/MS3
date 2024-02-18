package org.cswteams.ms3.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cswteams.ms3.control.doctor.IDoctorController;
import org.cswteams.ms3.dto.condition.NewConditionIDReturnDTO;
import org.cswteams.ms3.dto.condition.PermanentConditionDTO;
import org.cswteams.ms3.dto.condition.TemporaryConditionDTO;
import org.cswteams.ms3.dto.condition.UpdateConditionsDTO;
import org.cswteams.ms3.dto.medicalDoctor.MedicalDoctorInfoDTO;
import org.cswteams.ms3.dto.specialization.DoctorSpecializationDTO;
import org.cswteams.ms3.dto.specialization.SingleDoctorSpecializationDTO;
import org.cswteams.ms3.entity.condition.PermanentCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/doctors/")
public class DoctorRestEndpoint {
    @Autowired
    private IDoctorController doctorController;
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllDoctors() {
        Set<MedicalDoctorInfoDTO> medicalDoctorInfoDTOSet = doctorController.getAllDoctors();
        return new ResponseEntity<>(medicalDoctorInfoDTOSet, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{doctorId}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long doctorId) {
        MedicalDoctorInfoDTO medicalDoctorInfoDTO = doctorController.getDoctorById(doctorId);
        if (medicalDoctorInfoDTO != null) {
            return new ResponseEntity<>(medicalDoctorInfoDTO, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/user-profile/delete-specialization")
    public ResponseEntity<?> deleteDoctorSpecialization(@RequestBody() SingleDoctorSpecializationDTO doctorSpecializationDTO) {
        if(doctorSpecializationDTO.getDoctorID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                doctorController.deleteDoctorSpecialization(doctorSpecializationDTO.getDoctorID(), doctorSpecializationDTO.getSpecialization());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user-profile/add-specialization")
    public ResponseEntity<?> addDoctorSpecialization(@RequestBody() DoctorSpecializationDTO doctorSpecializationDTO) {
        if(doctorSpecializationDTO.getDoctorID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                doctorController.addDoctorSpecialization(doctorSpecializationDTO.getDoctorID(), doctorSpecializationDTO.getSpecializations());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/user-profile/delete-permanent-condition")
    public ResponseEntity<?> deleteDoctorPermanentCondition(@RequestBody() PermanentConditionDTO permanentConditionDTO) {
        if(permanentConditionDTO.getDoctorID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                doctorController.deleteDoctorPermanentCondition(permanentConditionDTO.getDoctorID(), permanentConditionDTO.getConditionID(), permanentConditionDTO.getLabel());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/user-profile/delete-temporary-condition")
    public ResponseEntity<?> deleteDoctorTemporaryCondition(@RequestBody() TemporaryConditionDTO temporaryConditionDTO) {
        if(temporaryConditionDTO.getDoctorID() < 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            try{
                doctorController.deleteDoctorTemporaryCondition(temporaryConditionDTO.getDoctorID(), temporaryConditionDTO.getConditionID(

                ),temporaryConditionDTO.getCondition());
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/user-profile/add-condition")
    public ResponseEntity<?> addDoctorConditions(@RequestBody() UpdateConditionsDTO updateConditionsDTO) throws JsonProcessingException {
        NewConditionIDReturnDTO newConditionIDReturnDTO;

        if(updateConditionsDTO.getDoctorID() < 0){
            newConditionIDReturnDTO = new NewConditionIDReturnDTO(-1L);
            return new ResponseEntity<>(newConditionIDReturnDTO,HttpStatus.BAD_REQUEST);
        }else{
            long conditionID = -1;
            try{
                conditionID = doctorController.addDoctorCondition(updateConditionsDTO.getDoctorID(), updateConditionsDTO.getCondition());
            }catch (Exception e){
                newConditionIDReturnDTO = new NewConditionIDReturnDTO(conditionID);
                return new ResponseEntity<>(newConditionIDReturnDTO,HttpStatus.INTERNAL_SERVER_ERROR);
            }
            newConditionIDReturnDTO = new NewConditionIDReturnDTO(conditionID);
            return new ResponseEntity<>(newConditionIDReturnDTO,HttpStatus.OK);
        }

    }

}
