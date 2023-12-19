package org.cswteams.ms3;


import org.cswteams.ms3.entity.Holiday;
import org.cswteams.ms3.jpa_constraints.validant.Validant;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
public class ValidantTestClass {

    @Validant
    public String validMethod(@Valid Holiday holiday) {
        return holiday.getName().substring(1) ;
    }
}
