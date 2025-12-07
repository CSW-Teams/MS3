package org.cswteams.ms3.security;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    // Dopo 1 tentativo fallito, chiediamo il captcha
    private static final int MAX_ATTEMPTS = 1;
    
    // Mappa in memoria: IP -> Numero Fallimenti
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
    }

    public void loginFailed(String key) {
        attemptsCache.merge(key, 1, Integer::sum);
    }

    public boolean isCaptchaRequired(String key) {
        return attemptsCache.getOrDefault(key, 0) >= MAX_ATTEMPTS;
    }
}