package org.cswteams.ms3.DBperTenant.utils.validators.admissible_values;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AdmissibleValuesValidator implements ConstraintValidator<AdmissibleValues, String> {

    private List<String> admissibleValues ;

    @Override
    public void initialize(AdmissibleValues constraintAnnotation) {
        this.admissibleValues = List.of(constraintAnnotation.values()) ;
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String o, ConstraintValidatorContext constraintValidatorContext) {
        if(o == null) return false ;
        return admissibleValues.contains(o);
    }
}