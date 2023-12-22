package org.cswteams.ms3.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorie/")
public class CategoriaUtenteRestEndpoint {
/*
    @Autowired
    private IControllerCategorieUtente controllerCategorieUtente;

    @Autowired
    private IControllerCategorie controllerCategorie;

    @RequestMapping(method = RequestMethod.GET, path = "/specializzazioni/")
    public ResponseEntity<?> leggiSpecializzazioni() throws ParseException {
        Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieSpecializzazioni();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turnazioni/")
    public ResponseEntity<?> leggiTurnazioni() throws ParseException {
        Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieTurnazioni();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stato/")
    public ResponseEntity<?> leggiStati() throws ParseException {
        Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieStato();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/stato/utente_id={idUtente}")
    public ResponseEntity<?> leggiCategorieUtente(@PathVariable Long idUtente) throws ParseException {
        if (idUtente != null) {
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiCategorieUtente(idUtente);
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
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiSpecializzazioniUtente(idUtente);
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
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiTurnazioniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rotazione_id={idRotazione}/utente_id={idUtente}")
    public ResponseEntity<?> deleteCategoriaUtente(@PathVariable Long idRotazione, @PathVariable Long idUtente) throws ParseException {
        if (idRotazione != null && idUtente != null) {
            try {
                controllerCategorieUtente.cancellaRotazione(idRotazione, idUtente);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/stato_id={idStato}/utente_id={idUtente}")
    public ResponseEntity<?> deleteStatoUtente(@PathVariable Long idStato, @PathVariable Long idUtente) throws ParseException {
        if (idStato != null && idUtente != null) {
            try {
                controllerCategorieUtente.cancellaStato(idStato, idUtente);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/turnazioni/utente_id={idUtente}")
    public ResponseEntity<?> aggiungiTurnazione(@RequestBody(required = true) CategoriaUtenteDTO categoriaUtenteDTO, @PathVariable Long idUtente) throws Exception {
        if (categoriaUtenteDTO != null) {
            return new ResponseEntity<>(controllerCategorieUtente.aggiungiTurnazioneUtente(categoriaUtenteDTO, idUtente), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    @RequestMapping(method = RequestMethod.POST, path = "/stato/utente_id={idUtente}")
    public ResponseEntity<?> aggiungiStato(@RequestBody(required = true) CategoriaUtenteDTO categoriaUtenteDTO, @PathVariable Long idUtente) throws Exception {
        if (categoriaUtenteDTO != null) {
            return new ResponseEntity<>(controllerCategorieUtente.aggiungiStatoUtente(categoriaUtenteDTO, idUtente), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


*/


}
