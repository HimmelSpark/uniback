package com.himmelspark.uniback.service;

import com.himmelspark.uniback.model.Tokens;
import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.repository.TokensRepository;
import com.himmelspark.uniback.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    public UserModel createUser(UserModel user) {
        try {
            return usersRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    @Transactional //TODO нужно ли? Какие эффекты будут? Будут ли эксепшоны?
    public void removeUserAndToken(UserModel user) {
        tokensRepository.deleteById(user.getId());
        usersRepository.delete(user);
    }

    public void enableUser(UserModel user) {
        usersRepository.enableUserByEmail(user.getEmail());
    }

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
        vToken.setUser(user);
        return tokensRepository.save(vToken);
    }

    public Tokens getVerificationToken(String token) {
        return tokensRepository.getVerificationTokenByToken(token);
    }

    public Tokens getVerificationTokenById(Long iD) {
            return tokensRepository.findTokensById(iD);
    }

    public void saveRegisteredUser(UserModel userModel) {
        usersRepository.save(userModel);
    }
}
