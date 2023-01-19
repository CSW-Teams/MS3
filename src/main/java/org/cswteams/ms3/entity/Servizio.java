package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Servizio {

    @Id
    private String nome;

    @Column
    @Enumerated
    @ElementCollection(targetClass = MansioneEnum.class)
    List<MansioneEnum> mansioni ;

    protected Servizio(){
    }

    public Servizio(String name){

        this.nome = name;
        this.mansioni = new ArrayList<>();

    }

    public Servizio(String name,List<MansioneEnum> mansioni){

        this.nome = name;
        this.mansioni = mansioni;

    }

}
