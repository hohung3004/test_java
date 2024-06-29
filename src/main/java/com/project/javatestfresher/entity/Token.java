package com.project.salebe.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {
    @Id
    @GeneratedValue(generator = "mygen")
    @GenericGenerator(name = "mygen", strategy = "com.project.salebe.util.IdGenerator")
    private String id;

    @Column(name = "token", length = 255)
    private String token;

    @Column(name = "refresh_token", length = 255)
    private String refreshToken;

    @Column(name = "token_type", length = 50)
    private String tokenType;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "refresh_expiration_date")
    private LocalDateTime refreshExpirationDate;


    private boolean revoked;
    private boolean expired;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
