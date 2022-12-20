package org.cswteams.ms3.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Servizio {

    @Id
    private String name;

    protected Servizio(){

    }

    public Servizio(String name){
        this.name = name;
    }
}
