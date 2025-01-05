package org.cswteams.ms3.multitenancyapp.utils.validators.input_integer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IntegerValueValidator implements ConstraintValidator<IntegerValue, String> {


    @Override
    public void initialize(IntegerValue constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null) return false;
        if (o.isEmpty()) return false;
        try {
            Integer.parseInt(o);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
