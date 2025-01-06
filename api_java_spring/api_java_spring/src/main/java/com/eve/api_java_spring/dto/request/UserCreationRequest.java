package com.eve.api_java_spring.dto.request;



import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Email(message = "USERNAME_INVALID") //message là enum error code được cấu hình các lỗi cần miêu tả
     String username;
    @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
     String password;
    @NotNull(message = "FIELD_INVALID")
     String firstName;
    @NotNull(message = "FIELD_INVALID")
     String lastName;
     LocalDate dob;
}
