package com.eve.api_java_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)//chuổi id không bị trùng lập
     String id;
     String username;
     String password;
     String firstName;
     String lastName;
     LocalDate dob;
     @ElementCollection // Đánh dấu đây là một tập hợp giá trị cơ bản. bởi vì entity k có các kiểu cụ thể như Set or List,...
     Set<String> roles;//tại sao dùng Set mà k dùng List, bởi vì Set các phần tử là Unit (không trùng lập)

}
