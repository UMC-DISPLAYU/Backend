package com.example.demo.domain.user.domain.entity;

import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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


    @Column(name = "univName", nullable = false)
    private String univName;


    @Column(name = "verificationCode", nullable = false)
    private String verificationCode;


    @Column(name = "expiresAt", nullable = false)
    private LocalDateTime expiresAt;


    @Column(name = "sentAt", nullable = false)
    private LocalDateTime sentAt;


    @Column(name = "verified", nullable = false)
    private boolean verified;



    private SchoolEmailVerification(
            String schoolEmail,
            String univName,
            String verificationCode,
            LocalDateTime expiresAt
    ) {

        this.schoolEmail = schoolEmail;
        this.univName = univName;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
        this.sentAt = LocalDateTime.now();
        this.verified = false;
    }



    public static SchoolEmailVerification create(
            String schoolEmail,
            String univName,
            String verificationCode
    ) {

        return new SchoolEmailVerification(
                schoolEmail,
                univName,
                verificationCode,
                LocalDateTime.now().plusMinutes(5)
        );
    }



    public boolean isExpired() {

        return LocalDateTime.now().isAfter(expiresAt);
    }



    public boolean canResend() {

        return LocalDateTime.now()
                .isAfter(sentAt.plusMinutes(1));
    }



    public boolean matchCode(String code) {

        return verificationCode.equals(code);
    }



    public boolean isVerified() {

        return verified;
    }



    public void verify() {

        this.verified = true;
    }
}