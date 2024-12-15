package org.cswteams.ms3.utils.validators.must_be_different;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MustBeDifferentValidator.class)
@Repeatable(MustBeDifferents.class)
public @interface MustBeDifferent {

    String message() default "{org.cswteams.ms3.utils.must_be_different.MustBeDifferent" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String first() ;

    String second() ;
}
