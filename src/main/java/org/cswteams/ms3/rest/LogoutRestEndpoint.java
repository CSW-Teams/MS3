package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.logout.LogoutController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/logout/")
public class LogoutRestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(LogoutRestEndpoint.class);

    private final LogoutController logoutController;

    public LogoutRestEndpoint(@Autowired LogoutController logoutController) {
        this.logoutController = logoutController;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> destroyAuthenticationToken(HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                logoutController.logout(token, userDetails.getEmail());
                logger.debug("Logout executed, token invalidated");
                return ResponseEntity.ok().build();
            } catch (UsernameNotFoundException e) {
                logger.error("Logout failed", e);
                return ResponseEntity.status(500).body("Logout failed: user not found");
            }
        }

        logger.error("Logout requested without Authorization header");
        return ResponseEntity.status(401).build();
    }
}
