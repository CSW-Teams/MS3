package org.cswteams.ms3.security;

import org.cswteams.ms3.services.UserDetailsService;
import org.cswteams.ms3.filters.JwtRequestFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilters jwtRequestFilters;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable CSRF protection as the application relies on token-based authentication (e.g., JWT).
        // CSRF protection is primarily used for session-based authentication and is not required in this context.
        http.csrf().disable()
                // Allow public access to the "/authenticate" endpoint.
                // This is necessary because users need to access this endpoint to obtain their authentication token.
                .authorizeRequests().antMatchers(HttpMethod.POST, "/authenticate").permitAll()

                // Require authentication for all other endpoints.
                // This ensures that all other resources are secured and can only be accessed by authenticated users with valid credentials.
                .anyRequest().authenticated()

                .and().formLogin()
                .loginPage("/login").permitAll()

                // Configure the session management policy.
                // Set the session creation policy to "STATELESS" as the application does not use sessions;
                // it relies solely on stateless JWT tokens for authentication and authorization.
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // This ensures that the JWT token is validated and the authentication context is set
        // before the standard UsernamePasswordAuthenticationFilter processes the request.
        // The filter intercepts each HTTP request to extract and verify the JWT token (if present).
        http.addFilterBefore(jwtRequestFilters, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();   // TODO: this is just for testing! Must encrypt the password!
    }
}
