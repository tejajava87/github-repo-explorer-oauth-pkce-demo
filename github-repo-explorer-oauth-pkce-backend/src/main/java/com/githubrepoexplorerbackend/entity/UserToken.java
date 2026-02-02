package com.githubrepoexplorerbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String githubLogin;

    @Column(nullable=false, length = 4000)
    private String accessToken;

    private String tokenType;
    private String scope;

    private Instant createdAt;
}
