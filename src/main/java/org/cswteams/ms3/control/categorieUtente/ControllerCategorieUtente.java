package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.control.utils.MappaCategoriaUtente;
import org.cswteams.ms3.dao.CategoriaUtenteDao;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;


@Service
public class ControllerCategorieUtente implements IControllerCategorieUtente {

    @Autowired
    CategoriaUtenteDao categoriaUtenteDao;

    @Autowired
    private CategorieDao categorieDao;

    @Override
    public Set<CategorieUtenteDTO> leggiCategorieUtente(Long id) {
        Set<CategorieUtenteDTO> categorieUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findStatoUtente(id));
        return categorieUtenteDTO;
    }

    @Override
    public Set<CategorieUtenteDTO> leggiSpecializzazioniUtente(Long id) {
        Set<CategorieUtenteDTO> specializzazioneUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findSpecializzazioniUtente(id));
        return specializzazioneUtenteDTO;
    }

    @Override
    public Set<CategorieUtenteDTO> leggiTurnazioniUtente(Long id) throws ParseException {
        Set<CategorieUtenteDTO> turnazioniUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findTurnazioniUtente(id));
        return turnazioniUtenteDTO;
    }

    @Override
    public CategoriaUtente aggiuntiTurnazioneUtente(CategorieUtenteDTO categorieUtenteDTO) throws Exception {

        if(categorieDao.findAllByNome(categorieUtenteDTO.getCategoria().getNome()) == null)
            throw new Exception("Non esiste una categoria con questo nome :"+categorieUtenteDTO.getCategoria().getNome());

        CategoriaUtente categorieUtente = MappaCategoriaUtente.categoriaUtenteDTOToEntity(categorieUtenteDTO);
        categoriaUtenteDao.save(categorieUtente);
        return  categorieUtente;
    }

}
