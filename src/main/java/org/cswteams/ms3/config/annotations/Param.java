package org.cswteams.ms3.config.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String key();
    boolean value();
}