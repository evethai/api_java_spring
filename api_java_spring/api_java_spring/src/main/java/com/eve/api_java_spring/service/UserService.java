package com.eve.api_java_spring.service;

import com.eve.api_java_spring.dto.request.UserCreationRequest;
import com.eve.api_java_spring.dto.request.UserUpdateRequest;
import com.eve.api_java_spring.entity.User;
import com.eve.api_java_spring.exception.AppException;
import com.eve.api_java_spring.exception.ErrorCode;
import com.eve.api_java_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired //được sử dụng để tự động inject repository vào service
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request) {
        User user = new User();

        if(userRepository.existsUserByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXITED);
        }

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    public User updateUser(String id,UserUpdateRequest request){
        User user = getUserById(id);//dùng hàm đã viết bên dưới
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

}
