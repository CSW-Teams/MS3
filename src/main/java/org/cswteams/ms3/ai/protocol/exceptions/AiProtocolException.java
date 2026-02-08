package org.cswteams.ms3.ai.protocol.exceptions;

import lombok.Getter;
import org.cswteams.ms3.ai.protocol.ValidationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class AiProtocolException extends RuntimeException {

    private final ErrorCategory category;
    private final ErrorCode code;
    private final List<ValidationError> details;

    AiProtocolException(ErrorCategory category, ErrorCode code, String message, Throwable cause) {
        this(category, code, message, null, cause);
    }

    AiProtocolException(ErrorCategory category, ErrorCode code, String message, List<ValidationError> details, Throwable cause) {
        super(message, cause);
        this.category = category;
        this.code = code;
        this.details = details == null ? null : Collections.unmodifiableList(new ArrayList<>(details));
    }

    public static AiProtocolException invalidJson(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.INVALID_JSON, message, cause);
    }

    public static AiProtocolException schemaMismatch(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.SCHEMA_MISMATCH, message, cause);
    }

    public static AiProtocolException schemaMismatch(String message, List<ValidationError> details, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.SCHEMA_MISMATCH, message, details, cause);
    }

    public static AiProtocolException typeMismatch(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.TYPE_MISMATCH, message, cause);
    }

    public static AiProtocolException transportFailure(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.TRANSPORT, ErrorCode.TRANSPORT_FAILURE, message, cause);
    }

    public static AiProtocolException timeout(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.TRANSPORT, ErrorCode.TIMEOUT, message, cause);
    }

    public static AiProtocolException businessFailure(String message) {
        return new AiProtocolException(ErrorCategory.BUSINESS_DOMAIN, ErrorCode.BUSINESS_FAILURE, message, null);
    }

    public static AiProtocolException partialSuccess(String message) {
        return new AiProtocolException(ErrorCategory.BUSINESS_DOMAIN, ErrorCode.PARTIAL_SUCCESS, message, null);
    }

    public static AiProtocolException entityNotFound(String message) {
        return new AiProtocolException(ErrorCategory.BUSINESS_DOMAIN, ErrorCode.ENTITY_NOT_FOUND, message, null);
    }

    public static AiProtocolException invalidFormat(String message) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.INVALID_FORMAT, message, null);
    }

    public static AiProtocolException invalidFormat(String message, Throwable cause) {
        return new AiProtocolException(ErrorCategory.APPLICATION_SCHEMA, ErrorCode.INVALID_FORMAT, message, cause);
    }

    public static AiProtocolException taskResolutionError(String message) {
        return new AiProtocolException(ErrorCategory.BUSINESS_DOMAIN, ErrorCode.TASK_RESOLUTION_ERROR, message, null);
    }

    public enum ErrorCategory { TRANSPORT, APPLICATION_SCHEMA, BUSINESS_DOMAIN }

    public enum ErrorCode {
        INVALID_JSON,
        SCHEMA_MISMATCH,
        TYPE_MISMATCH,
        TRANSPORT_FAILURE,
        TIMEOUT,
        BUSINESS_FAILURE,
        PARTIAL_SUCCESS,
        ENTITY_NOT_FOUND,
        INVALID_FORMAT,
        TASK_RESOLUTION_ERROR
    }
}
