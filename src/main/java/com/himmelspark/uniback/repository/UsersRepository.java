package com.himmelspark.uniback.repository;

import com.himmelspark.uniback.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<UserModel, Long> {

    UserModel getUserModelByEmail(String email);

//    @Query("select u.id, u.name from users as u")
//    List<UserModel> getAllUsers();

}
