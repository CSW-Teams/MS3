package org.cswteams.ms3.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NonNull;

/**
 * Modella se una categoria utente è vietata o necessaria per un turno
 */
@Entity
@Data
 public class UserCategoryPolicy {
    
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @NonNull
    private Categoria categoria;

    @ManyToOne
    @NonNull
    private Turno turno;
    
    /** 
     * La registrazione di questa policy nel db implica che tale categoria è da escludere
     * o da includere per un turno. Se così non fosse, non sarebbe stata registrata.
     * 
     * true  --> la categoria è necessaria
     * false --> la categoria è vietata
     * 
    */
    @NonNull
    @Enumerated(value = EnumType.STRING)
    private UserCategoryPolicyValue policy;
}
