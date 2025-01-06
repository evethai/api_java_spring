package com.eve.api_java_spring.controller;

import com.eve.api_java_spring.dto.request.ApiResponse;
import com.eve.api_java_spring.dto.request.UserCreationRequest;
import com.eve.api_java_spring.dto.request.UserUpdateRequest;
import com.eve.api_java_spring.dto.response.UserResponse;
import com.eve.api_java_spring.entity.User;
import com.eve.api_java_spring.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//đánh dấu class này là 1 lớp controller xử lý HTTP request trả về HTTP response
//kết hợp @Controller và @RequestBody tử động chuyển đổi object thanh Json
@RequestMapping("/users")
//khai báo endpoint cho các controller bên dưới
public class UserController {
    @Autowired
    //ịnject(tim service vào controller)
    //loại bỏ việc khởi tạo thủ công bằng new UserService
    private UserService userService;

    @PostMapping
    //đánh dấu là phương thức Post
    ApiResponse<User> createUser(@RequestBody @Valid  UserCreationRequest request) {
        //@RequestBody đánh dấu  tham số request được ánh xạ từ phần body của HTTP request và tự động chuyển đổi JSON thành object
        //@Valid có nhiệm vụ check tất cả các Rule đã được khai báo trong Object
        ApiResponse<User> response = new ApiResponse<>();
        response.setResult(userService.createUser(request));

        return response;
    }

    @GetMapping
    List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    UserResponse getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    UserResponse updateUser(@PathVariable String id,@RequestBody UserUpdateRequest request) {
        return userService.updateUser(id,request);
    }

    @DeleteMapping("/{id}")
    String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "User has been deleted";
    }

}
