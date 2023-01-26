package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.categorie.IControllerCategorie;
import org.cswteams.ms3.control.categorieUtente.IControllerCategorieUtente;
import org.cswteams.ms3.control.giustificaForzatura.IControllerGiustificaForzatura;
import org.cswteams.ms3.control.utils.MappaGiustificazioneForzaturaVincoli;
import org.cswteams.ms3.dao.GiustificazioneFozaturaDao;
import org.cswteams.ms3.dto.GiustificazioneForzaturaDto;
import org.cswteams.ms3.entity.Files;
import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/giustifica")
public class GiustificazioneForzaturaEndpoint {

    @Autowired
    private IControllerGiustificaForzatura iControllerGiustificaForzatura;


    @PostMapping("/carica")
    public ResponseEntity<String> caricaGiustificatura(@RequestParam("giustificatura") GiustificazioneForzaturaDto giustificazioneForzaturaVincoli) {
        try {
            iControllerGiustificaForzatura.saveGiustificazione(MappaGiustificazioneForzaturaVincoli.GiustificazioneForzaturaVincoliDtoToEntity(giustificazioneForzaturaVincoli));
            return ResponseEntity.status(HttpStatus.OK).body("Giustificazione caricata correttamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Giustificazione non caricata. Errore.");
        }
    }

}
