package com.himmelspark.uniback.service;

import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void createUser(UserModel user) {
        usersRepository.save(user);
    }

    public boolean checkUser(String email, String password) {
        UserModel db_user = usersRepository.getUserModelByEmail(email);
        return db_user.getPassword().equals(password);
    }
}
