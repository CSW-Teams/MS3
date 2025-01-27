package org.cswteams.ms3.config;

import org.cswteams.ms3.config.soft_delete.SoftDeleteWebFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterConfig {

    @Bean
    public FilterRegistrationBean<SoftDeleteWebFilter> loggingFilter(SoftDeleteWebFilter filter) {
        FilterRegistrationBean<SoftDeleteWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*"); // Definisci quali URL devono passare per questo filtro
        return registrationBean;
    }
}