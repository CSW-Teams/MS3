package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.dto.login.LoginRequestDTO;
import org.cswteams.ms3.dto.login.LoginResponseDTO;
import org.cswteams.ms3.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping("/login/")
public class LoginRestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(LoginRestEndpoint.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LoginController loginController;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDTO loginRequestDTO) {
        logger.debug("We reached createAuthenticationToken! {} {}", loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        try {
            // Try authenticating the user using credentials passed
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        final CustomUserDetails customUserDetails;
        try {
            customUserDetails = (CustomUserDetails) loginController.loadUserByUsername(loginRequestDTO.getEmail());
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
