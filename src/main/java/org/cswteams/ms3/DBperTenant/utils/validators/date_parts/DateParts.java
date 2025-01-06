package org.cswteams.ms3.DBperTenant.utils.validators.date_parts;

import org.cswteams.ms3.DBperTenant.utils.validators.day_and_month.DayAndMonthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DayAndMonthValidator.class)
@Repeatable(DatePartsCollector.class)
public @interface DateParts {

    String message() default "{org.cswteams.ms3.jpa_constraints.date_parts.DateParts" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String day() ;

    String month() ;

    String year() ;
}