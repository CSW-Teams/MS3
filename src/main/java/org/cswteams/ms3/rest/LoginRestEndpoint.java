package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.dto.login.LoginRequestDTO;
import org.cswteams.ms3.dto.login.LoginResponseDTO;
import org.cswteams.ms3.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.logging.Logger;

@RestController
@RequestMapping("/login/")
public class LoginRestEndpoint {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginController loginController;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDTO loginRequestDTO) {
        // TODO remove this log, or set it up only in debug mode
        Logger.getAnonymousLogger().info("We reached createAuthenticationToken! "
                + loginRequestDTO.getEmail() + " "
                + loginRequestDTO.getPassword() + " "
                + loginRequestDTO.getSystemActor());

        try {
            // Try authenticating the user using credentials passed
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword(),
                            Collections.singleton(new SimpleGrantedAuthority(loginRequestDTO.getSystemActor().toString()))
                    )
            );
        } catch (BadCredentialsException e) {
            // TODO understand what to do with message
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        final CustomUserDetails customUserDetails;
        try {
            customUserDetails = loginController.loadUserByUsernameAndRole(
                    loginRequestDTO.getEmail(),
                    loginRequestDTO.getSystemActor().toString()
            );
        } catch (Exception e) {
            // TODO understand what to do with message
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        // Generate token for logged user
        final String jwt = jwtTokenUtil.generateToken(customUserDetails);

        // TODO send a ResponseCookie instead of a Response Entity, cause this way the jwt could be stored in a cookie HTTP-Only

        return ResponseEntity.ok(new LoginResponseDTO(customUserDetails, jwt));
    }
}
