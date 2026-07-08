package com.example.demo.user.domain.entity;


import com.example.demo.global.entity.BaseTimeEntity;
import com.example.demo.user.domain.enums.Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "User")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private Provider provider;

    @Column(name = "providerId", nullable = false)
    private String providerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Builder.Default
    @Column(name = "isVerified", nullable = false)
    private boolean isVerified = false; // 작가 인증여부

    @Column(name = "socialEmail", nullable = false)
    private String socialEmail;

    @Column(name = "schoolEmail")
    private String schoolEmail;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Column(name = "nicknameChangedAt")
    private LocalDateTime nicknameChangedAt;
}