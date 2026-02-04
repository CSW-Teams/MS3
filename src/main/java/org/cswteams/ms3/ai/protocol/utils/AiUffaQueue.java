package org.cswteams.ms3.ai.protocol.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum AiUffaQueue {
    GEN,
    NIGHT,
    LONG;

    @JsonCreator
    public static AiUffaQueue fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid AiUffaQueue: null");
        }
        switch (value) {
            case "gen":
                return GEN;
            case "night":
                return NIGHT;
            case "long":
                return LONG;
            default:
                throw new IllegalArgumentException("Invalid AiUffaQueue: " + value);
        }
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(Locale.ROOT);
    }
}
