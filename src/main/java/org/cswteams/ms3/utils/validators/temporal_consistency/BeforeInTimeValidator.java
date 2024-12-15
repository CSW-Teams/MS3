package org.cswteams.ms3.utils.validators.temporal_consistency;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BeforeInTimeValidator implements ConstraintValidator<BeforeInTime, Object> {
    @Override
    public void initialize(BeforeInTime constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {

        try {
            BeforeInTime annotation = object.getClass().getAnnotation(BeforeInTime.class) ;
            String firstName = annotation.firstParam();
            String secondName = annotation.secondParam();

            Comparator comparator = annotation.comparator().getDeclaredConstructor().newInstance() ;
            Object object1, object2 ;

            Field firstField = object.getClass().getDeclaredField(firstName);
            firstField.setAccessible(true) ;
            object1 = firstField.get(object) ;

            Field secondField = object.getClass().getDeclaredField(secondName);
            secondField.setAccessible(true) ;
            object2 = secondField.get(object) ;

            return comparator.compare(object1, object2);

        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException e) {
            return false ;
        }
    }
}
