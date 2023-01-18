package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.cswteams.ms3.entity.CategoriaUtente;

import java.text.ParseException;
import java.util.Set;

public interface IControllerCategorieUtente {

    Set<CategorieUtenteDTO> leggiCategorieUtente(Long id)  throws ParseException;

    Set<CategorieUtenteDTO> leggiSpecializzazioniUtente(Long id)  throws ParseException;

    Set<CategorieUtenteDTO> leggiTurnazioniUtente(Long id)  throws ParseException;

    CategoriaUtente aggiuntiTurnazioneUtente(CategorieUtenteDTO categorieUtenteDTO) throws Exception;

}
