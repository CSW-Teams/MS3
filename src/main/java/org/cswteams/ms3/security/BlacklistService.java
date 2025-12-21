package org.cswteams.ms3.security;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

@Service
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

    public void add(String key) {
        blacklist.put(key, Boolean.TRUE);
    }

    public void remove(String key) {
        blacklist.invalidate(key);
    }

    public boolean isInBlackList(String key) {
        return blacklist.getIfPresent(key) != null;
    }
}