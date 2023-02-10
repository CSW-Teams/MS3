package org.cswteams.ms3.control.categorie;


import org.cswteams.ms3.control.utils.MappaCategoriaUtente;
import org.cswteams.ms3.control.utils.MappaCategoriePerTipo;
import org.cswteams.ms3.dao.CategorieDao;
import org.cswteams.ms3.dto.CategoriaDTO;
import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Set;

@Service
public class ControllerCategorie implements IControllerCategorie {

    @Autowired
    CategorieDao categorieDao;

    @Override
    public Set<CategoriaDTO> leggiCategorieStato() throws ParseException {
        return MappaCategoriePerTipo.categoriaSetEntityToDTO(categorieDao.findAllByTipo(TipoCategoriaEnum.STATO));
    }

    @Override
    public Set<CategoriaDTO> leggiCategorieSpecializzazioni() throws ParseException {
        return MappaCategoriePerTipo.categoriaSetEntityToDTO(categorieDao.findAllByTipo(TipoCategoriaEnum.SPECIALIZZAZIONE));
    }

    @Override
    public Set<CategoriaDTO> leggiCategorieTurnazioni() throws ParseException {
        return MappaCategoriePerTipo.categoriaSetEntityToDTO(categorieDao.findAllByTipo(TipoCategoriaEnum.TURNAZIONE));
    }
}
