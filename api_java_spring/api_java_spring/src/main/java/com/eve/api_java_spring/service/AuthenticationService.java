package com.eve.api_java_spring.service;

import com.eve.api_java_spring.dto.request.AuthenticationRequest;
import com.eve.api_java_spring.dto.request.IntrospectRequest;
import com.eve.api_java_spring.dto.response.AuthenticationResponse;
import com.eve.api_java_spring.dto.response.IntrospectResponse;
import com.eve.api_java_spring.entity.User;
import com.eve.api_java_spring.exception.AppException;
import com.eve.api_java_spring.exception.ErrorCode;
import com.eve.api_java_spring.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @NonFinal // đánh dấu để k inject và constructor của lombok
    @Value("${jwt.signerKey}") // đọc biến từ trong file application.yaml
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request)
    {
        var user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!authenticated){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .success(true)
                .build();


    }

    public IntrospectResponse introspectResponse(IntrospectRequest request) throws JOSEException, ParseException {
        // Lấy token từ request (request là một đối tượng chứa token cần kiểm tra)
        var token = request.getToken();

        // Tạo một verifier sử dụng thuật toán HMAC với khóa bí mật SIGNER_KEY
        // MACVerifier là một lớp để xác minh chữ ký HMAC của JWT với khóa bí mật
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        // Phân tích token JWT thành một đối tượng SignedJWT
        // SignedJWT là một lớp của thư viện JOSE để làm việc với JWT đã ký
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Lấy thời gian hết hạn (expiration time) của token từ JWT Claims Set
        // JWT Claims Set chứa các thông tin (claim) của token, bao gồm thời gian hết hạn
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Xác minh chữ ký của token bằng cách sử dụng verifier đã tạo
        // Nếu chữ ký hợp lệ, phương thức verify trả về true
        var verified = signedJWT.verify(verifier);

        // Tạo và trả về đối tượng IntrospectResponse với thông tin tính hợp lệ của token
        // Token được coi là hợp lệ nếu chữ ký đúng và thời gian hết hạn chưa đến
        return IntrospectResponse.builder()
                .valid(verified && expiration.after(new Date())) // Kiểm tra tính hợp lệ (chữ ký đúng và chưa hết hạn)
                .build(); // Xây dựng và trả về đối tượng IntrospectResponse
    }


    String generateToken(User user) {
        // Tạo Header cho JWT với thuật toán ký là ES512 (Elliptic Curve Algorithm)
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // Tạo Claims (payload) cho JWT, đây là thông tin người dùng và thời gian hết hạn của token
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())  // Thiết lập "subject" (người dùng) cho token
                .issuer("https://eve.com")  // Thiết lập "issuer" (người phát hành) của token
                .issueTime(new Date())  // Thiết lập "issueTime" là thời gian hiện tại
                .expirationTime(new Date(  // Thiết lập thời gian hết hạn (1 giờ kể từ thời điểm phát hành)
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("custom_name", "custom")  // Thêm một claim tùy chỉnh vào JWT
                .claim("scope",buildScope(user))
                .build();  // Xây dựng đối tượng JWTClaimsSet với các thông tin đã thiết lập

        // Tạo Payload từ JWTClaimsSet, chuyển các claims thành JSON
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // Tạo JWSObject (JSON Web Signature) kết hợp Header và Payload
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            // Ký JWSObject với một khóa bí mật sử dụng thuật toán HMAC (MACSigner)
            jwsObject.sign(new MACSigner(SIGNER_KEY));
            // Trả về token đã ký dưới dạng chuỗi
            return jwsObject.serialize();
        } catch (JOSEException e) {
            // In ra chi tiết lỗi
            System.err.println("JOSEException occurred: " + e.getMessage());
            e.printStackTrace();  // In ra stack trace chi tiết
            throw new AppException(ErrorCode.SIGNER_KEY_ERROR);
        }
    }

    private String buildScope(User user) {
        // Tạo một đối tượng StringJoiner với ký tự phân tách là dấu cách (" ")
        // StringJoiner được dùng để nối các chuỗi với một ký tự ngăn cách xác định.
        StringJoiner stringJoiner = new StringJoiner(" ");

        // Kiểm tra xem danh sách roles của user có rỗng hoặc null hay không.
        // CollectionUtils.isEmpty() trả về true nếu danh sách null hoặc không có phần tử.
        if (!CollectionUtils.isEmpty(user.getRoles())) {

            // Nếu danh sách roles không rỗng, duyệt qua từng phần tử trong danh sách.
            // Với mỗi role, thêm nó vào StringJoiner.
            user.getRoles().forEach(stringJoiner::add);
        }

        // Sau khi duyệt xong, chuyển tất cả các vai trò đã thêm trong StringJoiner thành một chuỗi.
        // Các vai trò sẽ được ngăn cách bởi một dấu cách (" ").
        return stringJoiner.toString();
    }
    //tại sao lại dùng hàm này: bởi vì đối với JWT của Spring lựa chọn việc đọc scope của các user
    //dưới dạng scope: ADMIN USER
    //được dùng để phân quyền

}
