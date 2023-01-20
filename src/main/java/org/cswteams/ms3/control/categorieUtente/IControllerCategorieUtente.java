package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.dto.CategoriaUtenteDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;

import java.text.ParseException;
import java.util.Set;

public interface IControllerCategorieUtente {

    Set<CategoriaUtenteDTO> leggiCategorieUtente(Long id)  throws ParseException;

    Set<CategoriaUtenteDTO> leggiSpecializzazioniUtente(Long id)  throws ParseException;

    Set<CategoriaUtenteDTO> leggiTurnazioniUtente(Long id)  throws ParseException;

    CategoriaUtente aggiungiTurnazioneUtente(CategoriaUtenteDTO c, Long utenteID) throws Exception;

}
