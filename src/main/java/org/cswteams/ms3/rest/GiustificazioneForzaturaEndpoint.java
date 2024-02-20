package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.giustificaForzatura.IControllerGiustificaForzatura;
import org.cswteams.ms3.dto.GiustificazioneForzaturaVincoliDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/justify")
public class GiustificazioneForzaturaEndpoint {

    @Autowired
    private IControllerGiustificaForzatura iControllerGiustificaForzatura;

    @RequestMapping(method = RequestMethod.POST, path = "/uploadJustification")
    public ResponseEntity<String> caricaGiustificazione(@RequestBody(required = true) GiustificazioneForzaturaVincoliDTO giustificazione) {
        try {
            iControllerGiustificaForzatura.saveGiustificazione(giustificazione);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Giustificazione caricata correttamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Giustificazione non caricata. Errore.");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            iControllerGiustificaForzatura.saveDelibera(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
}
