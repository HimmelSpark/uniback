package com.himmelspark.uniback.service;

import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.model.VerificationToken;
import com.himmelspark.uniback.repository.TokensRepository;
import com.himmelspark.uniback.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UsersRepository usersRepository;
    private final TokensRepository tokensRepository;

    @Autowired
    public UserService(UsersRepository usersRepository, TokensRepository tokensRepository) {
        this.usersRepository = usersRepository;
        this.tokensRepository = tokensRepository;
    }

    public UserModel createUser(UserModel user) {
        return usersRepository.save(user);
    }

    public boolean checkUser(String email, String password) {
        UserModel db_user = usersRepository.getUserModelByEmail(email);
        return db_user.getPassword().equals(password);
    }

    public VerificationToken createVerificationToken(UserModel user, String token) {
        VerificationToken vToken = new VerificationToken();
        vToken.setToken(token);
        vToken.setId(user.getId());
        return tokensRepository.save(vToken);
    }

    public VerificationToken getVerificationToken(String token) {
        return tokensRepository.getVerificationTokenByToken(token);
    }

    public void saveRegisteredUser(UserModel userModel) {
        usersRepository.save(userModel);
    }
}
