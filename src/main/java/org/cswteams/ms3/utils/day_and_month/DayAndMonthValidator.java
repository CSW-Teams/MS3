package org.cswteams.ms3.utils.day_and_month;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class DayAndMonthValidator implements ConstraintValidator<DayAndMonth, Object> {

    private String day ;

    private String month ;

    @Override
    public void initialize(DayAndMonth constraintAnnotation) {
        day = constraintAnnotation.day() ;
        month = constraintAnnotation.month();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Object dayObject, monthObject ;

            Field dayField = o.getClass().getDeclaredField(day) ;
            dayField.setAccessible(true);
            dayObject = dayField.get(o) ;

            Field monthField = o.getClass().getDeclaredField(month) ;
            monthField.setAccessible(true);
            monthObject = monthField.get(o) ;

            Integer day = (Integer) dayObject ;
            Integer month = (Integer) monthObject ;

            switch (month) {
                case 1 :
                case 3 :
                case 5 :
                case 7 :
                case 8 :
                case 10 :
                case 12 :
                    if(!(day > 0 && day <= 31)) return false ;
                    break;
                case 4 :
                case 6 :
                case 9 :
                case 11 :
                    if(!(day > 0 && day <= 30)) return false ;
                    break;
                case 2 :
                    if(!(day > 0 && day <= 29)) return false ;
                    break;
                default :
                    return false ;
            }

            return true ;

        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            return false ;
        }
    }
}
