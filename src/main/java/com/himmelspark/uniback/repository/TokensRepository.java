package com.himmelspark.uniback.repository;

import com.himmelspark.uniback.model.Tokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokensRepository extends JpaRepository<Tokens, Long> {

    Tokens getVerificationTokenByToken(String token);

}
