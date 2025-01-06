package com.eve.api_java_spring.service;

import com.eve.api_java_spring.dto.request.UserCreationRequest;
import com.eve.api_java_spring.dto.request.UserUpdateRequest;
import com.eve.api_java_spring.dto.response.UserResponse;
import com.eve.api_java_spring.entity.User;
import com.eve.api_java_spring.enums.Role;
import com.eve.api_java_spring.exception.AppException;
import com.eve.api_java_spring.exception.ErrorCode;
import com.eve.api_java_spring.mapper.UserMapper;
import com.eve.api_java_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // tự động tạo các field thành constructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) // vừa có accept level là private còn có final có nghĩa là k thể thay đổi
// khi dùng cả 2 anotation RequiredArgsConstructor và FieldDefaults có thể hiểu là sẽ tạo ra các biến có có kiểu là private final
public class UserService {
    //@Autowired //được sử dụng để tự động inject repository vào service
    // đây k phải là best practice để sử dụng inject
     UserRepository userRepository;
     UserMapper userMapper;
     PasswordEncoder passwordEncoder;

    public User createUser(UserCreationRequest request) {

        if(userRepository.existsUserByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXITED);
        }
/*        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setPassword(request.getPassword());
//        user.setFirstName(request.getFirstName());
//        user.setLastName(request.getLastName());
        user.setDob(request.getDob());*/
        //thực hiện có sự kết hợp của MapStruct
        User user = userMapper.toUser(request);//sử dụng auto mapper

        //sử dụng bcrypt để mã hóa password
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);// độ mạnh của mã hóa thông thường thì sẽ là 10, càng lớn mật khẩu càng khó giải mã
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //set mặt định role là USER
        HashSet<String> roles = new HashSet<>();
        //HashSet là một tập hợp không cho phép phần tử trùng lặp. Khi thêm vai trò mới vào roles,
        //HashSet sẽ tự động loại bỏ bất kỳ giá trị nào đã tồn tại trước đó.
        //Điều này đảm bảo rằng danh sách vai trò của người dùng không chứa các vai trò trùng lặp.
        roles.add(Role.USER.name());

        user.setRoles(roles);

        return userRepository.save(user);
    }

    public UserResponse updateUser(String id,UserUpdateRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        /*
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());
        */
        //thực hiện có sự kết hợp của MapStruct
        userMapper.updateUser(user, request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userMapper.toListUserResponse(userRepository.findAll());

    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
