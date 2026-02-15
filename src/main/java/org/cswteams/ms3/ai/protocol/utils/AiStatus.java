package org.cswteams.ms3.ai.protocol.utils;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum AiStatus {
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILURE;

    @JsonCreator
    public static AiStatus fromString(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if ("COMPLETE".equals(normalized) || "COMPLETED".equals(normalized)
                || "OK".equals(normalized) || "DONE".equals(normalized)) {
            return SUCCESS;
        }
        if ("PARTIAL".equals(normalized)) {
            return PARTIAL_SUCCESS;
        }
        return valueOf(normalized);
    }
}
