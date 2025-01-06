package org.cswteams.ms3.DBperTenant.utils.validators.start_end_day_month;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartEndMonthValidator.class)
public @interface StartEndDayMonth {

    String message() default "{org.cswteams.ms3.jpa_constraints.start_end_day_month.StartEndDayMonth" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };


    String startDay() ;

    String startMonth() ;

    String endDay() ;

    String endMonth() ;
}