package org.cswteams.ms3.utils.validators.must_be_different;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class MustBeDifferentValidator implements ConstraintValidator<MustBeDifferent, Object> {

    private String first ;
    private String second ;

    @Override
    public void initialize(MustBeDifferent constraintAnnotation) {
        first = constraintAnnotation.first() ;
        second = constraintAnnotation.second() ;
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Object firstObj, secondObj ;

            Field dayField = o.getClass().getDeclaredField(first) ;
            dayField.setAccessible(true);
            firstObj = dayField.get(o) ;

            Field monthField = o.getClass().getDeclaredField(second) ;
            monthField.setAccessible(true);
            secondObj = monthField.get(o) ;

            return firstObj.equals(secondObj) ;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false ;
        }
    }
}
