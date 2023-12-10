package org.cswteams.ms3.config;

import org.cswteams.ms3.jpa_constraints.validant.ValidantAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorAspectBean {

    @Bean
    public ValidantAspect getValidantAspect() {
        return new ValidantAspect() ;
    }
}
