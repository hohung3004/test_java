package com.project.salebe.repository;


import com.project.salebe.entity.Token;
import com.project.salebe.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByUser(UserEntity user);
    Token findByToken(String token);
    Token findByRefreshToken(String token);
}

