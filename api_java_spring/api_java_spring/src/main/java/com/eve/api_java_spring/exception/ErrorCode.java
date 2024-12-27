package com.eve.api_java_spring.exception;

public enum ErrorCode {
    USER_EXITED(400, "User Exited"),
    UNCATEGORIZED(9999, "Uncategorized Error"),
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
