package org.cswteams.ms3.rest;

import org.cswteams.ms3.services.UserDetailsService;
import org.cswteams.ms3.entity.CustomUserDetails;
import org.cswteams.ms3.entity.auth.AuthenticationRequest;
import org.cswteams.ms3.entity.auth.AuthenticationResponse;
import org.cswteams.ms3.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class AuthenticateRestEndpoint {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        Logger.getAnonymousLogger().info("We reached createAuthenticationToken! "
                + authenticationRequest.getUsername() + " "
                + authenticationRequest.getPassword() + " "
                + authenticationRequest.getRole());

        try {
            // Try authenticating the user using credentials passed
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // TODO understand what to do with message
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        final CustomUserDetails customUserDetails;
        try {
            customUserDetails = userDetailsService.loadUserByUsernameAndRole(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getRole()
            );
        } catch (Exception e) {
            // TODO understand what to do with message
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        // Generate token for logged user
        final String jwt = jwtTokenUtil.generateToken(customUserDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
