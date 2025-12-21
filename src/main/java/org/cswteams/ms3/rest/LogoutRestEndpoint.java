package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.logout.LogoutController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> destroyAuthenticationToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutController.logout(token);
            logger.debug("Logout executed, token invalidated");
        } else {
            logger.debug("Logout requested without Authorization header");
        }

        return ResponseEntity.ok().build();
    }

}