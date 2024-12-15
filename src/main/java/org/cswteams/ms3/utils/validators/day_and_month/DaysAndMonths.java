package org.cswteams.ms3.utils.validators.day_and_month;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DaysAndMonths {
    DayAndMonth[] value() ;
}
