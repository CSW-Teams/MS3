package org.cswteams.ms3.multitenancyapp.utils.validators.start_end_day_month;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class StartEndMonthValidator implements ConstraintValidator<StartEndDayMonth, Object> {

    private String startDay ;
    private String startMonth ;
    private String endDay ;
    private String endMonth ;

    @Override
    public void initialize(StartEndDayMonth constraintAnnotation) {
        this.startDay = constraintAnnotation.startDay();
        this.startMonth = constraintAnnotation.startMonth();
        this.endDay = constraintAnnotation.endDay();
        this.endMonth = constraintAnnotation.endMonth();

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {

        try {
            Integer startDay, startMonth, endDay, endMonth ;

            Field firstField = o.getClass().getDeclaredField(this.startDay);
            firstField.setAccessible(true) ;
            startDay = (Integer) firstField.get(o) ;

            Field secondField = o.getClass().getDeclaredField(this.startMonth);
            secondField.setAccessible(true) ;
            startMonth = (Integer) secondField.get(o) ;

            Field thirdField = o.getClass().getDeclaredField(this.endDay);
            thirdField.setAccessible(true) ;
            endDay = (Integer) thirdField.get(o) ;

            Field fourthField = o.getClass().getDeclaredField(this.endMonth);
            fourthField.setAccessible(true) ;
            endMonth = (Integer) fourthField.get(o) ;

            if(endMonth < startMonth) return false ;
            if(endMonth.equals(startMonth)) {
                return endDay >= startDay;
            }

            return true ;
        }catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            return false ;
        }
    }
}
