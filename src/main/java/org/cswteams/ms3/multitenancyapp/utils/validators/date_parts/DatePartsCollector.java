package org.cswteams.ms3.multitenancyapp.utils.validators.date_parts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePartsCollector {

    DateParts[] value() ;
}