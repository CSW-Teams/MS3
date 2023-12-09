package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiustificazioneFozaturaDao  extends JpaRepository<GiustificazioneForzaturaVincoli,Long > {

    @Override
    <S extends GiustificazioneForzaturaVincoli> List<S> saveAll(Iterable<S> iterable);
}
