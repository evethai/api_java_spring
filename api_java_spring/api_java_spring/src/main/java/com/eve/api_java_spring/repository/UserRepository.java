package com.eve.api_java_spring.repository;

import com.eve.api_java_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    //JpaRepository sẽ sử dụng entity User và String là giá trị ID của entity đó ở đây là String
    boolean existsUserByUsername(String username);
    //đây là 1 phương thức tùy chỉnh (custom method)
    /*Tên phương thức theo quy tắc đặt tên của Spring Data JPA sẽ được dịch thành một câu truy vấn để kiểm tra xem có
        bản ghi User nào với username tương ứng hay không.
        đây là  điều kì diệu của Spring là khi chúng ta khai báo với cấu trúc có sẳn ví dụ như existsUserBy hoặc
        existsBy + field có trong Entity thì been sẽ tự động thao tác query những câu này mà không cần code method.*/


    Optional<User> findUserByUsername(String username);
}
//class cung cấp 1 interface sử dụng Spring data JPA nhầm thực hiện các thao tác đối với database như CRUD