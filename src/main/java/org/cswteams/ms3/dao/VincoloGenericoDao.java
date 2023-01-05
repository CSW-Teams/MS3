package org.cswteams.ms3.dao;

import java.util.List;

import org.cswteams.ms3.control.vincoli.Vincolo;

public interface VincoloGenericoDao {
    List<Vincolo> findAll();
}
