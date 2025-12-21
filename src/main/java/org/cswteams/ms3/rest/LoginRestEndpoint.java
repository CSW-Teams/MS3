package org.cswteams.ms3.rest;

import org.cswteams.ms3.control.login.LoginController;
import org.cswteams.ms3.dto.login.CustomUserDetails;
import org.cswteams.ms3.dto.login.LoginRequestDTO;
import org.cswteams.ms3.dto.login.LoginResponseDTO;
import org.cswteams.ms3.security.BlacklistService;
import org.cswteams.ms3.utils.JwtUtil;
import org.cswteams.ms3.utils.TurnstileService;
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



import javax.servlet.http.HttpServletRequest; // Usare 'jakarta.servlet' se si usa Spring Boot 3+

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

    @Autowired
    private TurnstileService turnstileService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequestDTO loginRequestDTO) {
        logger.debug("We reached createAuthenticationToken! {}", loginRequestDTO.getEmail());

        // 1. Otteniamo l'IP del client
        String ip = getClientIP();

        // 2. CONTROLLO PREVENTIVO: Questo IP o l'email sono in blacklist?
        if (blacklistService.isInBlackList(ip) || blacklistService.isInBlackList(loginRequestDTO.getEmail())) {
            
            // Se serve il captcha, verifichiamo se il token è presente nel DTO
            String token = loginRequestDTO.getTurnstileToken();

            if (token == null || token.isEmpty()) {
                // Manca il token -> Errore 401
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Verifica di sicurezza richiesta!");
            }

            // Validiamo il token con Cloudflare
            boolean isCaptchaValid = turnstileService.validateToken(token);
            
            if (!isCaptchaValid) {
                // Token falso o scaduto -> Errore 400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Captcha non valido o scaduto!");
            }
        }

        // 3. TENTATIVO DI LOGIN STANDARD (Codice originale avvolto in try-catch modificato)
        try {
            // Try authenticating the user using credentials passed
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getEmail(),
                            loginRequestDTO.getPassword()
                    )
            );
            
            // SE SIAMO QUI, IL LOGIN È RIUSCITO:
            // Resettiamo il contatore dei fallimenti per questo IP ed email
            blacklistService.remove(ip);
            blacklistService.remove(loginRequestDTO.getEmail());

        } catch (BadCredentialsException e) {
            // LOGIN FALLITO:
            // 1. Incrementiamo il contatore dei fallimenti
            blacklistService.add(ip);
            blacklistService.add(loginRequestDTO.getEmail());

            // 2. Restituiamo 401 UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenziali errate.");
        }
        
        final CustomUserDetails customUserDetails;
        try {
            customUserDetails = (CustomUserDetails) loginController.loadUserByUsername(loginRequestDTO.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }

        // Generate token for logged user
        final String jwt = jwtTokenUtil.generateToken(customUserDetails);

        return ResponseEntity.ok(new LoginResponseDTO(customUserDetails, jwt));
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}