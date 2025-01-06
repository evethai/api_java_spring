package com.eve.api_java_spring.exception;

public enum ErrorCode {
    USER_EXITED(400, "User Exited"),
    UNCATEGORIZED(9999, "Uncategorized Error"),
    INVALID_KEY(4000, "Invalid Key"),
    USERNAME_INVALID(4000, "Username must be a valid email address"),
    PASSWORD_INVALID(4000, "Password must be between 8 and 20 characters"),
    FIELD_INVALID(4000, "Field must be not null"),
    USER_NOT_EXIST(4000, "User Not Exist"),
    UNAUTHORIZED(4001, "Unauthorized"),
    SIGNER_KEY_ERROR(4002, "Signer Key Error"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
