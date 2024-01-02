package org.cswteams.ms3.dao;

import org.cswteams.ms3.entity.GiustificazioneForzaturaVincoli;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GiustificazioneFozaturaDao  extends JpaRepository<GiustificazioneForzaturaVincoli,Long > {

    @Override
    <S extends GiustificazioneForzaturaVincoli> List<S> saveAll(Iterable<S> iterable);
}
