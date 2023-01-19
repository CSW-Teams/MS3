package org.cswteams.ms3.control.categorie;

import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.CategoriaDTO;
import org.cswteams.ms3.dto.RegistraAssegnazioneTurnoDTO;
import org.cswteams.ms3.entity.AssegnazioneTurno;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.exception.AssegnazioneTurnoException;

import java.text.ParseException;
import java.util.Set;

public interface IControllerCategorie {
    Set<CategoriaDTO> leggiCategorieStato() throws ParseException;

    Set<CategoriaDTO> leggiCategorieSpecializzazioni() throws ParseException;

    Set<CategoriaDTO> leggiCategorieTurnazioni() throws ParseException;

}
