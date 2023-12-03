package org.cswteams.ms3.entity;

import lombok.Data;
import org.cswteams.ms3.enums.MansioneEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    /** Lista di mansioni che è possibile svolgere durante un turno di questo servizio */
    List<MansioneEnum> mansioni ;

    protected Servizio(){
    }

    public Servizio(@NotNull String name){

        this.nome = name;
        this.mansioni = new ArrayList<>();

    }

    public Servizio(@NotNull String name, @NotNull List<MansioneEnum> mansioni){

        this.nome = name;
        this.mansioni = mansioni;

    }

}
