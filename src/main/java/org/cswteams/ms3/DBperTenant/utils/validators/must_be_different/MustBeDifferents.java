package org.cswteams.ms3.DBperTenant.utils.validators.must_be_different;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MustBeDifferents {

    MustBeDifferent[] value() ;
}