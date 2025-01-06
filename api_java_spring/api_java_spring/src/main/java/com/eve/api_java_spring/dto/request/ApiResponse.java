package com.eve.api_java_spring.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonInclude sử dụng nhằm mục đích nếu field nào bị null trong quá trình serialize từ obj sang Json sẽ k được thêm vào Json
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
     int code = 200;
     String message;
     T result;
}

//class chuẩn hóa các response của hệ thống
