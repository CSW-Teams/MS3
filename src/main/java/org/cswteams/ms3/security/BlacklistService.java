package org.cswteams.ms3.security;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

@Service
/**
 * In-memory blacklist used by post-baseline security checks for short-lived invalidation keys.
 *
 * <p>Technical workaround: entries are cache-based and temporary, unlike the persisted JWT blacklist.</p>
 */
public class BlacklistService {
    private final Cache<String, Boolean> blacklist;

    public BlacklistService() {
        blacklist = Caffeine.newBuilder()
                // Le entries decadono dopo 1 ora dall'ultimo accesso in lettura o scrittura
                .expireAfterAccess(1, TimeUnit.HOURS)
                // La cache puo' contenere massimo 10k entries
                .maximumSize(10_000)
                .build();
    }

    /**
     * Adds a key to the temporary blacklist.
     */
    public void add(String key) {
        blacklist.put(key, Boolean.TRUE);
    }

    /**
     * Removes a key from the temporary blacklist before automatic expiry.
     */
    public void remove(String key) {
        blacklist.invalidate(key);
    }

    /**
     * Checks whether the key is currently blocked by cache state.
     */
    public boolean isInBlackList(String key) {
        return blacklist.getIfPresent(key) != null;
    }
}