package com.project.javatestfresher.repository;




import com.project.javatestfresher.entity.Token;
import com.project.javatestfresher.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByUser(UserEntity user);
    Token findByToken(String token);
}

