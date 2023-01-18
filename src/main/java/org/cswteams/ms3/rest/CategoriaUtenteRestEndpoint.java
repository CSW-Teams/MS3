package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.categorieUtente.IControllerCategorieUtente;
import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Set;

@RestController
@RequestMapping("/categorie/")
public class CategoriaUtenteRestEndpoint {

    @Autowired
    private IControllerCategorieUtente controllerCategorieUtente;

    @RequestMapping(method = RequestMethod.GET, path = "/utente_id={idUtente}")
    public ResponseEntity<?> leggiCategorieUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set<CategorieUtenteDTO> c = controllerCategorieUtente.leggiCategorieUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/specializzazioni/utente_id={idUtente}")
    public ResponseEntity<?> leggiSpecializzazioniUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set<CategorieUtenteDTO> c = controllerCategorieUtente.leggiSpecializzazioniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
