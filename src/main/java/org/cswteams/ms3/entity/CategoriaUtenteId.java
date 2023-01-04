package org.cswteams.ms3.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.cswteams.ms3.enums.CategoriaUtentiEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

public class CategoriaUtenteId implements Serializable {

    private CategoriaUtentiEnum categoria;

    private Long utenteId;

    public CategoriaUtenteId(CategoriaUtentiEnum categoria, Long utente){
        this.categoria = categoria;
        this.utenteId = utente;
    }

    public CategoriaUtenteId() {

    }
}
