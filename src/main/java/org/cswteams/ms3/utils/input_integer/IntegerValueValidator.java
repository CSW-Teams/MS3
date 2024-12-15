package org.cswteams.ms3.utils.input_integer;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

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
