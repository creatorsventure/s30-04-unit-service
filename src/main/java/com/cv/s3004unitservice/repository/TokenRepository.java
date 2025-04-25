package com.cv.s3004unitservice.repository;

import com.cv.s3002unitservicepojo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String>, JpaSpecificationExecutor<Token> {

    Optional<Token> findByTokenHashAndRevokedFalse(String tokenHash);

    Optional<Token> findByUserIdAndRevokedFalse(String username);
}
