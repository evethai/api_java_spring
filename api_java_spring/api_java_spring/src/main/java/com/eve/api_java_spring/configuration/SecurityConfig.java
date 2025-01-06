package com.eve.api_java_spring.configuration;

import com.eve.api_java_spring.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration // Đánh dấu đây là một lớp cấu hình Spring, sẽ được Spring quản lý.
@EnableWebSecurity // Kích hoạt tính năng bảo mật Spring Security.
public class SecurityConfig {

    // Khai báo danh sách các endpoint công khai (không yêu cầu xác thực).
    private final String[] PUBLIC_ENDPOINTS =
            {"/users", "/auth/login", "/auth/introspect"};
    private final String[] GET_ADMIN = {"/users"};

    // Sử dụng annotation @Value để lấy giá trị khóa bí mật từ file cấu hình (application.yaml).
    @Value("${jwt.signerKey}")
    private String signerKey;

    /**
     * Bean định nghĩa chuỗi bộ lọc bảo mật. Hàm này được Spring gọi khi khởi tạo ứng dụng.
     * @param httpSecurity: Đối tượng để cấu hình bảo mật HTTP.
     * @return SecurityFilterChain đã được cấu hình.
     * @throws Exception nếu có lỗi trong quá trình cấu hình.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình phân quyền truy cập:
        httpSecurity.authorizeHttpRequests(authorizeRequests ->
                // Các endpoint POST trong PUBLIC_ENDPOINTS không cần xác thực.
                authorizeRequests.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET,GET_ADMIN).hasRole(Role.ADMIN.name())
                        // Mọi yêu cầu khác đều cần được xác thực.
                        .anyRequest().authenticated());

        // Cấu hình xác thực qua JWT bằng OAuth2 Resource Server:
        httpSecurity.oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer.jwt(jwtConfigurer ->
                        // Sử dụng hàm jwtDecoder để giải mã JWT.
                        jwtConfigurer.decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

        // Vô hiệu hóa CSRF vì ứng dụng có thể không sử dụng form-based authentication.
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // Trả về cấu hình SecurityFilterChain đã hoàn thiện.
        return httpSecurity.build();
    }

    /**
     * Bean định nghĩa cách giải mã JWT (JSON Web Token).
     * Hàm này được gọi mỗi khi cần xử lý một JWT để xác thực người dùng.
     * @return JwtDecoder được cấu hình với khóa bí mật (signerKey) và thuật toán HS512.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        // Tạo khóa bí mật từ giá trị signerKey với thuật toán HS512.
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");

        // Trả về một JwtDecoder sử dụng thuật toán MAC (HS512) để giải mã JWT.
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return converter;
    }
    //mục đích của hàm này dùng để convert lại JWT response, chuẩn setup là scope nhưng chúng ta convert thành Role
    //nhầm sử dụng được hasRole thay cho hasAuthority

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }//Khai báo PasswordEncoder biến này vào application context để sử dụng ở tất cả mọi nơi, không cần khai báo lại

}

