package org.cswteams.ms3.control.utils;


import org.cswteams.ms3.dto.CategoriaUtenteDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class MappaCategoriaUtente {


    public static CategoriaUtente categoriaUtenteDTOToEntity(CategoriaUtenteDTO dto) {
        Categoria c = dto.getCategoria();
        DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME;
        System.out.println(dto.getInizioValidita());
        LocalDate inizio = LocalDate.parse(dto.getInizioValidita(), df);
        LocalDate fine = LocalDate.parse(dto.getFineValidita(), df);
        return new CategoriaUtente(c, inizio, fine);
    }

    public static CategoriaUtenteDTO categoriaUtenteToDTO(CategoriaUtente entity) {
        return new CategoriaUtenteDTO(entity.getId(),entity.getCategoria(),entity.getInizioValidità().toString(),entity.getFineValidità().toString());
    }

    public static Set<CategoriaUtenteDTO> categoriaUtenteToDTO(Set<CategoriaUtente> categoriaUtente) {
        Set<CategoriaUtenteDTO> categorieUtenteDTOS = new HashSet<>();
        for (CategoriaUtente entity : categoriaUtente) {
            categorieUtenteDTOS.add(categoriaUtenteToDTO(entity));
        }
        return categorieUtenteDTOS;
    }


}
