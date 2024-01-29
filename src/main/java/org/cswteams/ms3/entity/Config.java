package org.cswteams.ms3.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * This class contains all the configurations useful for all parts of the system
 */
@Entity
@Data
public class Config {

    /**
     * The name of the configuration
     */
    @Id
    private String name;

    /**
     * Has the application booted before?
     */
    private boolean firstBoot;
}
