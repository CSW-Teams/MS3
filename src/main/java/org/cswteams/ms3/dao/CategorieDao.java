package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Categoria;
import org.cswteams.ms3.enums.TipoCategoriaEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CategorieDao extends JpaRepository<Categoria, String> {

    Categoria findAllByNome(String name);

    Set<Categoria> findAllByTipo(TipoCategoriaEnum tipo);

}
