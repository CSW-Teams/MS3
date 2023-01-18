package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.categorieUtente.IControllerCategorieUtente;
import org.cswteams.ms3.control.utils.MappaCategoriaUtente;
import org.cswteams.ms3.control.utils.MappaUtenti;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dao.TurnoDao;
import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.exception.IllegalAssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/categorie/")
public class CategoriaUtenteRestEndpoint {

    @Autowired
    private IControllerCategorieUtente controllerCategorieUtente;

    @Autowired
    private CategorieDao categorieDao;

    @RequestMapping(method = RequestMethod.GET, path = "/stato/utente_id={idUtente}")
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

    @RequestMapping(method = RequestMethod.GET, path = "/turnazioni/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurnazioniUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set<CategorieUtenteDTO> c = controllerCategorieUtente.leggiTurnazioniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> aggiungiTurnazione(@RequestBody CategorieUtenteDTO categoria) {
        if (categoria != null) {
            try {
                if(categorieDao.findAllByNome(categoria.getCategoria().getNome()) == null)
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                CategoriaUtente categoria_new = controllerCategorieUtente.aggiuntiTurnazioneUtente(categoria);
                return new ResponseEntity<>(categoria_new, HttpStatus.ACCEPTED);

            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }



}
