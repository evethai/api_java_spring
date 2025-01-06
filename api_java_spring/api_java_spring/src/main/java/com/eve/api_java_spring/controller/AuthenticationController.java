package com.eve.api_java_spring.controller;


import com.eve.api_java_spring.dto.request.ApiResponse;
import com.eve.api_java_spring.dto.request.AuthenticationRequest;
import com.eve.api_java_spring.dto.request.IntrospectRequest;
import com.eve.api_java_spring.dto.response.AuthenticationResponse;
import com.eve.api_java_spring.dto.response.IntrospectResponse;
import com.eve.api_java_spring.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)//nếu k khai báo gì thì Lombok sẽ overwrite thành private dùng để inject
public class AuthenticationController {

    AuthenticationService  authenticationService;
    private final RestClient.Builder builder;

    @PostMapping("/login")//endpoint
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest)//ánh xạ trong body
    {
        // Thực hiện xác thực
        AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);

         /*Trả về ApiResponse chứa kết quả xác thực
        return ApiResponse.<AuthenticationResponse>builder()
                .result(AuthenticationResponse.builder()
                        .success(result) // true nếu xác thực thành công, false nếu không
                        .build())
                .build();*/

        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();

        /*ApiResponse.<AuthenticationResponse>builder(): Tạo một đối tượng Builder cho lớp ApiResponse với
         kiểu dữ liệu là AuthenticationResponse.
        .result(AuthenticationResponse.builder()...build()): Gọi AuthenticationResponse.builder(),
         thiết lập thuộc tính success từ kết quả của phương thức authenticate,
         và sau đó gọi build() để tạo đối tượng AuthenticationResponse.
        .build(): Cuối cùng, gọi build() trên ApiResponse.Builder để tạo đối tượng ApiResponse*/
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspectResponseApiResponse (@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result = authenticationService.introspectResponse(introspectRequest);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

}
