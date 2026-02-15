package org.cswteams.ms3.ai.broker;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;

/**
 * In-memory rolling token usage tracker (60-second window).
 */
public class AiTokenUsageTracker {

    private static final Duration WINDOW = Duration.ofSeconds(60);

    private final Clock clock;
    private final Map<AgentProvider, Deque<TokenSample>> usageByProvider = new EnumMap<>(AgentProvider.class);

    public AiTokenUsageTracker() {
        this(Clock.systemUTC());
    }

    AiTokenUsageTracker(Clock clock) {
        this.clock = clock;
    }

    public synchronized int projectedTpm(AgentProvider provider, int requestTokens, int responseTokens) {
        int current = currentTpm(provider);
        return current + Math.max(0, requestTokens) + Math.max(0, responseTokens);
    }

    public synchronized void recordUsage(AgentProvider provider, int requestTokens, int responseTokens) {
        Instant now = clock.instant();
        Deque<TokenSample> bucket = usageByProvider.computeIfAbsent(provider, key -> new ArrayDeque<>());
        evictExpired(bucket, now);
        int totalTokens = Math.max(0, requestTokens) + Math.max(0, responseTokens);
        bucket.addLast(new TokenSample(now, totalTokens));
    }

    public synchronized int currentTpm(AgentProvider provider) {
        Instant now = clock.instant();
        Deque<TokenSample> bucket = usageByProvider.computeIfAbsent(provider, key -> new ArrayDeque<>());
        evictExpired(bucket, now);

        int total = 0;
        for (TokenSample tokenSample : bucket) {
            total += tokenSample.tokens;
        }
        return total;
    }

    private void evictExpired(Deque<TokenSample> bucket, Instant now) {
        while (!bucket.isEmpty()) {
            TokenSample sample = bucket.peekFirst();
            if (sample == null || Duration.between(sample.recordedAt, now).compareTo(WINDOW) <= 0) {
                break;
            }
            bucket.removeFirst();
        }
    }

    private static class TokenSample {
        private final Instant recordedAt;
        private final int tokens;

        private TokenSample(Instant recordedAt, int tokens) {
            this.recordedAt = recordedAt;
            this.tokens = tokens;
        }
    }
}
