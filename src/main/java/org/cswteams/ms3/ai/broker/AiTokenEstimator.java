package org.cswteams.ms3.ai.broker;

/**
 * Lightweight token estimator used when provider-native tokenizers are unavailable.
 */
public class AiTokenEstimator {

    private static final int MIN_OUTPUT_TOKENS = 256;
    private static final int MAX_OUTPUT_TOKENS = 4096;

    public int estimateInputTokens(AiBrokerRequest request) {
        return estimateTextTokens(nullToEmpty(request.getInstructions()) + "\n" + nullToEmpty(request.getToonPayload()));
    }

    public int estimateExpectedOutputTokens(AiBrokerRequest request) {
        int inputTokens = estimateInputTokens(request);
        int estimated = inputTokens / 2;
        if (estimated < MIN_OUTPUT_TOKENS) {
            return MIN_OUTPUT_TOKENS;
        }
        return Math.min(estimated, MAX_OUTPUT_TOKENS);
    }

    int estimateTextTokens(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        int charEstimate = (int) Math.ceil(text.length() / 4.0);
        String[] words = text.trim().split("\\s+");
        int wordEstimate = (int) Math.ceil(words.length * 1.3);
        return Math.max(charEstimate, wordEstimate);
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
