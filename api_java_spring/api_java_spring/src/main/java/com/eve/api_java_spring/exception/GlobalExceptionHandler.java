package com.eve.api_java_spring.exception;


import com.eve.api_java_spring.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
//đánh dấu class này là 1 thành phần toàn cụ xử lý ngoại lệ hoặc các hoạt động chung cho tất cả Controller
//trong class này cụ thể là xử lý lỗi chung cho các Controller

public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    //Gắn với một phương thức cụ thể để chỉ định rằng phương thức đó sẽ xử lý loại ngoại lệ được chỉ định
    ResponseEntity<ApiResponse> handleException(Exception e) {
        //đối với method handleRuntimException sẽ xử lý các trường hợp liên quan đến Exception và ngoại lệ con của nó
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED.getCode()); //hard code
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }//đoạn code này là level 1 xa

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(e.getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }//đoạn code này level 2 với việc có thể chủ động cấu hình error code và message
}

//class dùng để xứ lý các ngoại lệ phát sinh bên trong ứng dụng 1 cách tập trung