package org.cswteams.ms3.utils.day_and_month;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DayAndMonthValidator.class)
@Repeatable(DaysAndMonths.class)
public @interface DayAndMonth {

    String message() default "{org.cswteams.ms3.jpa_constraints.day_and_month.DayAndMonth" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String day() ;

    String month() ;
}
