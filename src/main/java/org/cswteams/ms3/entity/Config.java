package org.cswteams.ms3.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * Racchiude tutte le configurazioni utili a tutte le parti del
 * sistema
 */
@Entity
@Data
public class Config {
    
    /**
     * The name of the configuration
     */
    @Id
    private String name;

    /** Has the application booted before? */
    private boolean firstBoot;
}
