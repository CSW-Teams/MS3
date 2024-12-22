package org.cswteams.ms3.security;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.enums.SystemActor;
import org.cswteams.ms3.exception.login.InvalidAuthException;
import org.cswteams.ms3.exception.login.InvalidEmailAddressException;
import org.cswteams.ms3.exception.login.InvalidRoleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private LoginController loginController;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Role not provided"));

        // Carica l'utente dal DB usando il servizio
        CustomUserDetails customUserDetails;
        try {
            customUserDetails = loginController.authenticateUser(email, password, SystemActor.valueOf(role));
        } catch (InvalidEmailAddressException | InvalidRoleException e) {
            throw new InvalidAuthException(e.getMessage());
        }

        // TODO capire dove fare il controllo della password criptata, se qui o nel controller direttamente
//        // Verifica la password
//        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
//            throw new BadCredentialsException("Invalid credentials");
//        }

        // Se tutto è valido, restituisci l'Authentication
        return new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
