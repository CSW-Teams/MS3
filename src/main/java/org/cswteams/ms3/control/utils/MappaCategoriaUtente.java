package org.cswteams.ms3.control.utils;


import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dto.CategoriaUtenteDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class MappaCategoriaUtente {


    public static CategoriaUtente categoriaUtenteDTOToEntity(CategoriaUtenteDTO dto) {
        return new CategoriaUtente(dto.getCategoria(), dto.getInizioValidita(), dto.getFineValidita());
    }

    public static CategoriaUtenteDTO categoriaUtenteToDTO(CategoriaUtente entity) {
        CategoriaUtenteDTO dto = new CategoriaUtenteDTO(entity.getCategoria(),entity.getInizioValidità(),entity.getFineValidità());
        return dto;
    }

    public static Set<CategoriaUtenteDTO> categoriaUtenteToDTO(Set<CategoriaUtente> categoriaUtente) {
        Set<CategoriaUtenteDTO> categorieUtenteDTOS = new HashSet<>();
        for (CategoriaUtente entity : categoriaUtente) {
            categorieUtenteDTOS.add(categoriaUtenteToDTO(entity));
        }
        return categorieUtenteDTOS;
    }


}
