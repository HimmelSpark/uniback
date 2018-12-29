package com.himmelspark.uniback.repository;

import com.himmelspark.uniback.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokensRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken getVerificationTokenByToken(String token);
}
