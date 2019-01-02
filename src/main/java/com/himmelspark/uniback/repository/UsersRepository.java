package com.himmelspark.uniback.repository;

import com.himmelspark.uniback.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UsersRepository extends JpaRepository<UserModel, Long> {
    UserModel getUserModelByEmail(String email);

    @Modifying
    @Query("UPDATE UserModel SET enabled=true WHERE email=?1")
    UserModel enableUserByEmail(String email);

    @Modifying
    @Query("UPDATE UserModel SET enabled=false WHERE email=?1")
    UserModel disableUserByEmail(String email);
}
