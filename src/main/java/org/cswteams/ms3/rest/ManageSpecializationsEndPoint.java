package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.specializations.SpecializationsController;
import org.cswteams.ms3.control.user.UserController;
import org.cswteams.ms3.dto.DoctorDTO;
import org.cswteams.ms3.dto.singleDoctorSpecializations.SingleDoctorSpecializationsDTO;
import org.cswteams.ms3.entity.Doctor;
import org.cswteams.ms3.entity.Specialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/specializations/")
public class ManageSpecializationsEndPoint {
    /*@Autowired
    private IControllerCategorieUtente controllerCategorieUtente;

    @Autowired
    private IControllerCategorie controllerCategorie;*/

    @Autowired
    private SpecializationsController specializationsController;


    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getAllPossibleSpecializations() throws ParseException {
        /*Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieSpecializzazioni();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/doctor_id={doctorID}")
    public ResponseEntity<?> getSingleDoctorSpecializations(@PathVariable Long doctorID) throws ParseException {
        SingleDoctorSpecializationsDTO doctorSpecializations = specializationsController.getSingleDoctorSpecializations(doctorID);
        if(doctorSpecializations != null){
            return new ResponseEntity<>(doctorSpecializations, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turnazioni/")
    public ResponseEntity<?> leggiTurnazioni() throws ParseException {
        /*Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieTurnazioni();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/stato/")
    public ResponseEntity<?> leggiStati() throws ParseException {
        /*Set<CategoriaDTO> categorie = controllerCategorie.leggiCategorieStato();
        return new ResponseEntity<>(categorie, HttpStatus.FOUND);*/
        return null;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/stato/utente_id={idUtente}")
    public ResponseEntity<?> leggiCategorieUtente(@PathVariable Long idUtente) throws ParseException {
        /*if (idUtente != null) {
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiCategorieUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/specializzazioni/utente_id={idUtente}")
    public ResponseEntity<?> leggiSpecializzazioniUtente(@PathVariable Long idUtente) throws ParseException {
        /*if (idUtente != null) {
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiSpecializzazioniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/turnazioni/utente_id={idUtente}")
    public ResponseEntity<?> leggiTurnazioniUtente(@PathVariable Long idUtente) throws ParseException {
        /*if (idUtente != null) {
            Set<CategoriaUtenteDTO> c = controllerCategorieUtente.leggiTurnazioniUtente(idUtente);
            if (c == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>( c, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rotazione_id={idRotazione}/utente_id={idUtente}")
    public ResponseEntity<?> deleteCategoriaUtente(@PathVariable Long idRotazione, @PathVariable Long idUtente) throws ParseException {
        /*if (idRotazione != null && idUtente != null) {
            try {
                controllerCategorieUtente.cancellaRotazione(idRotazione, idUtente);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/stato_id={idStato}/utente_id={idUtente}")
    public ResponseEntity<?> deleteStatoUtente(@PathVariable Long idStato, @PathVariable Long idUtente) throws ParseException {
        /*if (idStato != null && idUtente != null) {
            try {
                controllerCategorieUtente.cancellaStato(idStato, idUtente);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (DatabaseException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/turnazioni/utente_id={idUtente}")
    public ResponseEntity<?> aggiungiTurnazione(@RequestBody(required = true)Specialization specialization, @PathVariable Long idUtente) throws Exception {
        /*if (categoriaUtenteDTO != null) {
            return new ResponseEntity<>(controllerCategorieUtente.aggiungiTurnazioneUtente(categoriaUtenteDTO, idUtente), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }
    @RequestMapping(method = RequestMethod.POST, path = "/stato/utente_id={idUtente}")
    public ResponseEntity<?> aggiungiStato(@RequestBody(required = true)  Specialization specialization, @PathVariable Long idUtente) throws Exception {
        /*if (categoriaUtenteDTO != null) {
            return new ResponseEntity<>(controllerCategorieUtente.aggiungiStatoUtente(categoriaUtenteDTO, idUtente), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
        return null;
    }
}
