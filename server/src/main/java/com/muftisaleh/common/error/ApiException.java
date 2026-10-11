package com.muftisaleh.common.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Every deliberate failure this service raises, in one type.
 * Messages are bilingual (English + Bangla) so the admin can act on them.
 */
@Getter
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String code;
    private final String messageBn;

    public ApiException(HttpStatus status, String code, String message, String messageBn) {
        super(message);
        this.status = status;
        this.code = code;
        this.messageBn = messageBn;
    }

    public static ApiException notFound(String what, String whatBn) {
        return new ApiException(HttpStatus.NOT_FOUND, "not_found", what, whatBn);
    }

    public static ApiException badRequest(String code, String message, String messageBn) {
        return new ApiException(HttpStatus.BAD_REQUEST, code, message, messageBn);
    }

    public static ApiException unauthorized(String message, String messageBn) {
        return new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", message, messageBn);
    }

    public static ApiException forbidden(String message, String messageBn) {
        return new ApiException(HttpStatus.FORBIDDEN, "forbidden", message, messageBn);
    }

    public static ApiException conflict(String code, String message, String messageBn) {
        return new ApiException(HttpStatus.CONFLICT, code, message, messageBn);
    }

    public static ApiException tooManyRequests(String message, String messageBn) {
        return new ApiException(HttpStatus.TOO_MANY_REQUESTS, "too_many_requests", message, messageBn);
    }

    public static ApiException reasonRequired() {
        return badRequest("reason_required", "Write a reason before rejecting.", "বাতিল করার আগে কারণ লিখুন।");
    }
}
