package org.cswteams.ms3.multitenancyapp.jpa_constraints.temporal_consistency;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeforeInTimeValidator.class)
public @interface BeforeInTime {

    String message() default "{org.cswteams.ms3.jpa_constraints.temporal_consistency.BeforeInTime" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


    String firstParam() ;

    String secondParam() ;


    Class<? extends Comparator> comparator() ;
}
