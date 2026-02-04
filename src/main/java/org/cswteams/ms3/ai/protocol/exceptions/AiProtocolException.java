package org.cswteams.ms3.ai.protocol.exceptions;

import lombok.Getter;

@Getter
public class AiProtocolException extends RuntimeException {

    private final ErrorCategory category;
    private final ErrorCode code;

    AiProtocolException(ErrorCategory category, ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.category = category;
        this.code = code;
    }

    public static AiProtocolException invalidJson(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.INVALID_JSON, message, cause);
    }

    public static AiProtocolException schemaMismatch(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.SCHEMA_MISMATCH, message, cause);
    }

    public static AiProtocolException typeMismatch(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.TYPE_MISMATCH, message, cause);
    }

    public enum ErrorCategory { TRANSPORT, APPLICATION_SCHEMA, BUSINESS_DOMAIN }
    public enum ErrorCode { INVALID_JSON, SCHEMA_MISMATCH, TYPE_MISMATCH }
}
