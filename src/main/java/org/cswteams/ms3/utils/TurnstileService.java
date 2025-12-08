package org.cswteams.ms3.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

@Service
public class TurnstileService {

    @Value("${cloudflare.turnstile.secret}")
    private String secretKey;

    @Value("${cloudflare.turnstile.url}")
    private String verifyUrl;

    @Autowired
    private RestTemplate restTemplate;

    public boolean validateToken(String token) {
        // Se non c'è token fallisce
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 1. Prepariamo il payload form-data (come richiesto da Cloudflare)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", secretKey);
        body.add("response", token);
        // Opzionale: body.add("remoteip", ipAddress); 

        try {
            // 2. Chiamata POST all'API di Cloudflare
            @SuppressWarnings({ "unchecked", "null" })
            Map<String, Object> response = restTemplate.postForObject(verifyUrl, body, Map.class);

            // 3. Analizziamo la risposta JSON: { "success": true, ... }
            if (response != null && response.containsKey("success")) {
                return (Boolean) response.get("success");
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Fail secure: se Cloudflare non risponde, blocchiamo per sicurezza
        }
    }
}