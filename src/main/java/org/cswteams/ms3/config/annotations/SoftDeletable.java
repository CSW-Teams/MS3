package org.cswteams.ms3.config.annotations;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
//@FilterDef(name = "softDeleteFilter", parameters = {
//        @ParamDef(name = "deleted", type = "boolean")
//})
public @interface SoftDeletable {
    String filterName() default "softDeleteFilter";  // Nome del filtro

    // Parametri personalizzati che possono essere passati all'annotazione
    Param[] params() default {
            @Param(key = "deleted", value = false) // Imposta un valore di default per il parametro "deleted"
    };
}
