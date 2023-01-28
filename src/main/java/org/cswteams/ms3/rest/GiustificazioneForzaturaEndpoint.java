package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.giustificaForzatura.IControllerGiustificaForzatura;
import org.cswteams.ms3.control.utils.MappaGiustificazioneForzaturaVincoli;
import org.cswteams.ms3.dao.FilesDAO;
import org.cswteams.ms3.dto.GiustificazioneForzaturaDto;
import org.cswteams.ms3.entity.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/giustifica")
public class GiustificazioneForzaturaEndpoint {

    @Autowired
    private IControllerGiustificaForzatura iControllerGiustificaForzatura;

    @Autowired
    private FilesDAO filesDAO;


    @RequestMapping(method = RequestMethod.POST, path = "/caricaGiustifica")
    public ResponseEntity<String> caricaGiustificatura(@RequestParam("giustificatura") GiustificazioneForzaturaDto giustificazioneForzaturaVincoli) {
        try {
            iControllerGiustificaForzatura.saveGiustificazione(MappaGiustificazioneForzaturaVincoli.GiustificazioneForzaturaVincoliDtoToEntity(giustificazioneForzaturaVincoli));
            return ResponseEntity.status(HttpStatus.OK).body("Giustificazione caricata correttamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Giustificazione non caricata. Errore.");
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/caricaFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") Files file) {
        String message = "";
        try {
            filesDAO.save(file);
            message = "Uploaded the file successfully: " + file.getMultipartFile().getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getMultipartFile().getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

//    @PostMapping(value = "/caricaFiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity uploadFile(@RequestParam MultipartFile[] files) {
//
//        for (MultipartFile file : files) {
//            try {
//                //filesDAO.save(file);
//                System.out.println("Uploaded the file successfully: " + file.getMultipartFile().getOriginalFilename());
//            } catch (Exception e) {
//                System.out.println("Could not upload the file: " + file.getMultipartFile().getOriginalFilename() + ". Error: " + e.getMessage());
//            }
//        }
//        return ResponseEntity.ok().build();
//    }



}
