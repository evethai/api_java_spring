package com.eve.api_java_spring.mapper;

import com.eve.api_java_spring.dto.request.UserCreationRequest;
import com.eve.api_java_spring.dto.request.UserUpdateRequest;
import com.eve.api_java_spring.dto.response.UserResponse;
import com.eve.api_java_spring.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
//đánh dấu là 1 interface mapper, khi biên dịch MapStruc sẽ tự động tạo 1 class để thực hiện logic ánh xạ
//componentModel = "spring" Cấu hình này chỉ định rằng class mapper được tạo bởi MapStruct sẽ là một Spring Bean.
//Điều này có nghĩa là bạn có thể sử dụng mapper này với cơ chế Dependency Injection (DI) của Spring mà không cần tạo thủ công.
public interface UserMapper {
    User toUser(UserCreationRequest request);
    //Đây là một phương thức ánh xạ:
    //Input: Một object kiểu UserCreationRequest (thường là DTO từ client).
    //Output: Một object kiểu User (thường là Entity để lưu vào cơ sở dữ liệu).
    //Khi MapStruct biên dịch, nó sẽ tự động tạo ra mã Java để ánh xạ các thuộc tính của UserCreationRequest sang User dựa trên tên thuộc tính.


    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    //@MappingTarget: Đây là một annotation đặc biệt của MapStruct dùng để chỉ định rằng đối tượng user sẽ là đối tượng đích  muốn cập nhật.
    //Điều này có nghĩa là MapStruct sẽ không tạo một đối tượng User mới, mà sẽ sử dụng đối tượng user hiện có và cập nhật các trường của nó từ UserUpdateRequest.
    //Chỉ những thuộc tính nào có trong UserUpdateRequest sẽ được cập nhật, còn các thuộc tính khác trong user sẽ không thay đổi.

    @Mapping(source = "firstName",target = "lastName")// giống như formember bên C#
    @Mapping(target = "dob", ignore = true) // ignore là k có map field này, bỏ trống field này đi
    UserResponse toUserResponse (User user);
    //tương tự method trên method này với mục đích map từ entity thành dto trả về

    List<UserResponse> toListUserResponse (List<User> users);
    // đối với list
}
//class sử dụng MapStruct để thực hiện ánh xạ mapper giữa entity và dto
