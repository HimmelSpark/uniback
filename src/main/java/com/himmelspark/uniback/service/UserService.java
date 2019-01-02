package com.himmelspark.uniback.service;

import com.himmelspark.uniback.model.Tokens;
import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.repository.TokensRepository;
import com.himmelspark.uniback.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final TokensRepository tokensRepository;

    @Autowired
    public UserService(UsersRepository usersRepository, TokensRepository tokensRepository) {
        this.usersRepository = usersRepository;
        this.tokensRepository = tokensRepository;
    }

//    @Transactional
    public UserModel createUser(UserModel user) {
        try {
            return usersRepository.save(user);
        } catch (Exception e) {
            return null; //TODO эксепшон вроде как анчекд будет, разобраться, как сделать так, чтобы не возникало
        }
    }

    @Transactional
    public UserModel enableUser(UserModel user) {
        return usersRepository.enableUserByEmail(user.getEmail());
    }

    @Transactional
    public UserModel disableUser(UserModel user) {
        return usersRepository.disableUserByEmail(user.getEmail());
    }

    public boolean checkUser(String email, String password) {
        UserModel db_user = usersRepository.getUserModelByEmail(email);
        return db_user.getPassword().equals(password);
    }

    public Tokens createVerificationToken(UserModel user, String token) {
        Tokens vToken = new Tokens(user.getId(), token);
        vToken.setToken(token);
//        vToken.setId(user.getId());
        vToken.setUser(user);
        return tokensRepository.save(vToken);
    }

    public Tokens getVerificationToken(String token) {
        return tokensRepository.getVerificationTokenByToken(token);
    }

    public void saveRegisteredUser(UserModel userModel) {
        usersRepository.save(userModel);
    }
}
