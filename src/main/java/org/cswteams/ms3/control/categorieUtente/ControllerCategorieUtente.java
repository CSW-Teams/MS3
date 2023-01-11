package org.cswteams.ms3.control.categorieUtente;

import org.cswteams.ms3.control.utils.MappaAssegnazioneTurni;
import org.cswteams.ms3.control.utils.MappaCategoriaUtente;
import org.cswteams.ms3.dao.CategoriaUtenteDao;
import org.cswteams.ms3.dto.AssegnazioneTurnoDTO;
import org.cswteams.ms3.dto.CategorieUtenteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class ControllerCategorieUtente implements IControllerCategorieUtente {

    @Autowired
    CategoriaUtenteDao categoriaUtenteDao;
    @Override
    public Set<CategorieUtenteDTO> leggiCategorieUtente(Long id) {
        Set<CategorieUtenteDTO> categorieUtenteDTO = MappaCategoriaUtente.categoriaUtenteToDTO(categoriaUtenteDao.findCategorieUtente(id));
        return categorieUtenteDTO;
    }
}
