package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorieDao extends JpaRepository<Categoria, String> {
    List<Categoria> findAllByNome(String nome);

}
