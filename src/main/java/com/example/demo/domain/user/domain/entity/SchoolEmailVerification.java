package com.example.demo.domain.user.domain.entity;

import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@Entity
@Table(name = "SchoolEmailVerification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolEmailVerification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verificationId")
    private Long id;

    @Column(name = "schoolEmail", nullable = false)
    private String schoolEmail;

    @Column(name = "verificationCode", nullable = false)
    private String verificationCode;

    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;


    private SchoolEmailVerification(
            String schoolEmail,
            String verificationCode,
            LocalDateTime expiresAt) {

        this.schoolEmail = schoolEmail;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }


    public static SchoolEmailVerification create(
            String schoolEmail,
            String verificationCode) {

        return new SchoolEmailVerification(
                schoolEmail,
                verificationCode,
                LocalDateTime.now().plusMinutes(5));
    }


    public boolean isExpired() {
        return LocalDateTime.now()
                .isAfter(expiresAt);
    }
}