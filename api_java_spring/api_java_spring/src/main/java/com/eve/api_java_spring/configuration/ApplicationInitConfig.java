package com.eve.api_java_spring.configuration;

import com.eve.api_java_spring.entity.User;
import com.eve.api_java_spring.enums.Role;
import com.eve.api_java_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j //logger
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Kiểm tra xem user "admin" đã tồn tại trong cơ sở dữ liệu chưa
            if (userRepository.findUserByUsername("admin").isEmpty()) {
                // Tạo một HashSet để chứa role ADMIN
                var role = new HashSet<String>();
                role.add(Role.ADMIN.name());

                // Tạo đối tượng User với thông tin mặc định
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin")) // Encode password
                        .roles(role)
                        .build();

                // Lưu user vào cơ sở dữ liệu
                userRepository.save(user);

                // Log thông tin
                log.info("Account Admin has been created with default username and password: admin");
            }
        };
    }
}
