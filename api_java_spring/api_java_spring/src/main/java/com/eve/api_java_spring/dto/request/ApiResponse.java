package com.eve.api_java_spring.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude sử dụng nhằm mục đích nếu field nào bị null trong quá trình serialize từ obj sang Json sẽ k được thêm vào Json
public class ApiResponse<T> {
    private int code;
    private String message;
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

//class chuẩn hóa các response của hệ thống
