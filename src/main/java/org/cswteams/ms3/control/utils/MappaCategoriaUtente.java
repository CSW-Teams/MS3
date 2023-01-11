package org.cswteams.ms3.control.utils;


import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.cswteams.ms3.dto.UtenteDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.CategoriaUtente;
import org.cswteams.ms3.entity.Turno;
import org.cswteams.ms3.entity.Utente;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MappaCategoriaUtente {

    //To DO (Da integrare per l'edit delle categorie)
    public static CategoriaUtente categoriaUtenteDTOToEntity(CategorieUtenteDTO dto) {
        return new CategoriaUtente();
    }

    public static CategorieUtenteDTO categoriaUtenteToDTO(CategoriaUtente entity) {
        CategorieUtenteDTO dto = new CategorieUtenteDTO(entity.getCategoria(),entity.getInizioValidità(),entity.getFineValidità());
        return dto;
    }

    public static Set<CategorieUtenteDTO> categoriaUtenteToDTO(Set<CategoriaUtente> categoriaUtentes) {
        Set<CategorieUtenteDTO> categorieUtenteDTOS = new HashSet<>();
        for (CategoriaUtente entity : categoriaUtentes) {
            categorieUtenteDTOS.add(categoriaUtenteToDTO(entity));
        }
        return categorieUtenteDTOS;
    }


}
