package org.cswteams.ms3.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Liberatoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    public Liberatoria() {

    }

    public Liberatoria(String fileName, String type, byte[] data){
        this.data = data;
        this.name = fileName;
        this.type = type;
    }

}
