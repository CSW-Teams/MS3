package org.cswteams.ms3.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(TwoFactorProperties.class)
public class TwoFactorConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
