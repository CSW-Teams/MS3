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
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
