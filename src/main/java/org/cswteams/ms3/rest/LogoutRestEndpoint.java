package org.cswteams.ms3.rest;


import org.cswteams.ms3.control.logout.LogoutController;
import org.cswteams.ms3.dto.logout.LogoutRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout/")
public class LogoutRestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(LogoutRestEndpoint.class);

    @Autowired
    private LogoutController logoutController;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> destroyAuthenticationToken(@RequestBody LogoutRequestDTO logoutRequestDTO){
        return (ResponseEntity<?>) ResponseEntity.ok();
    }

}
