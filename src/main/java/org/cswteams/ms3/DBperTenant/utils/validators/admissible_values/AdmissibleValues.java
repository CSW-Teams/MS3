package org.cswteams.ms3.DBperTenant.utils.validators.admissible_values;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AdmissibleValuesValidator.class)
public @interface AdmissibleValues {

    String message() default "{org.cswteams.ms3.jpa_constraints.admissible_values.AdmissibleValues" +
            "message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    String[] values() ;
}