package org.cswteams.ms3.utils.input_integer;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IntegerValueValidator.class)
public @interface IntegerValue {
    String message() default "{org.cswteams.ms3.util.input_integer.IntegerValue" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
