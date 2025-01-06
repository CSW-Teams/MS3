package org.cswteams.ms3.DBperTenant.utils.validators.date_parts;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.LocalDate;

public class DatePartsValidator implements ConstraintValidator<DateParts, Object> {

    private String dayFieldName ;
    private String monthFieldName ;

    private String yearFieldName ;
    @Override
    public void initialize(DateParts constraintAnnotation) {
        dayFieldName = constraintAnnotation.day() ;
        monthFieldName = constraintAnnotation.month() ;
        yearFieldName = constraintAnnotation.year() ;
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Integer day, month, year ;

            Field dayField = o.getClass().getDeclaredField(dayFieldName);
            dayField.setAccessible(true);
            day = (Integer) dayField.get(o) ;

            Field monthField = o.getClass().getDeclaredField(monthFieldName);
            monthField.setAccessible(true);
            month = (Integer) monthField.get(o) ;

            Field yearField = o.getClass().getDeclaredField(yearFieldName);
            yearField.setAccessible(true);
            year = (Integer) yearField.get(o) ;

            LocalDate.of(year, month, day) ;

            return true ;

        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException | DateTimeException | NullPointerException e) {
            return false ;
        }
    }
}