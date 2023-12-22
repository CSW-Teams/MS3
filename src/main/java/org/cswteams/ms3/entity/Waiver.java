package org.cswteams.ms3.entity;


import lombok.Data;

import javax.persistence.*;

/**
 * Represents a formal document or a string text, containing the cause of
 * a shift swap (by a planner) or of an absence (by a doctor) due to specific condition
 */
@Entity
@Data
public class Waiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiver_id", nullable = false)
    private Long id;

    private String name;

    private String type;

    @Lob
    private byte[] data;

    /**
     * Default constructor used for lombok @Data annotation
     */
    protected Waiver() {

    }

    /**
     * Public construct
     * @param fileName The name of the file, containing the medical certificate or some other legal document
     * @param type The text string which act as a description for the waiver
     * @param data Actual data containing the file
     */
    public Waiver(String fileName, String type, byte[] data){
        this.data = data;
        this.name = fileName;
        this.type = type;
    }

}
