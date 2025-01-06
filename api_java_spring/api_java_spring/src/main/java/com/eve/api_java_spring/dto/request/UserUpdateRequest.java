package com.eve.api_java_spring.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Data // khai báo các getter setter toString constructor cho class
@NoArgsConstructor // tạo 1 obj không cần truyền tham số
@AllArgsConstructor // tạo 1 obj bắt buộc truyền toàn bộ tham số
@Builder // giúp tạo các obj có thể build trực tiếp các tham số vào obj đó giúp code clean hơn
// UserUpdateRequest rq = UserUpdateRequest.builder().password("1234").firstName("Jone")...build();
@FieldDefaults(level = AccessLevel.PRIVATE) // defaults tất cả các field bên trong class sẽ là  Accept Modifiers duy nhất
public class UserUpdateRequest {
     String password;
     String firstName;
     String lastName;
     LocalDate dob;
}
