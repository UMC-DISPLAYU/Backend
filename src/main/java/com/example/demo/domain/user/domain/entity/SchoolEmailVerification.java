package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "SchoolEmailVerification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolEmailVerification extends BaseTimeEntity {

  private static final int MAX_FAILED_ATTEMPTS = 5;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(name = "failedAttemptCount", nullable = false)
  private int failedAttemptCount;

  private SchoolEmailVerification(
      User user,
      String schoolEmail,
      String univName,
      String verificationCode,
      LocalDateTime expiresAt) {

    this.user = user;
    this.schoolEmail = schoolEmail;
    this.univName = univName;
    this.verificationCode = verificationCode;
    this.expiresAt = expiresAt;
    this.sentAt = LocalDateTime.now(ZoneOffset.UTC);
    this.verified = false;
    this.failedAttemptCount = 0;
  }

  public static SchoolEmailVerification create(
      User user, String schoolEmail, String univName, String verificationCode) {

    return new SchoolEmailVerification(
        user,
        schoolEmail,
        univName,
        verificationCode,
        LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5)); // 인증코드 유효시간: 5분
  }

  public boolean isExpired() {

    return !LocalDateTime.now(ZoneOffset.UTC).isBefore(expiresAt);
  }

  public boolean canResend() {

    return LocalDateTime.now(ZoneOffset.UTC).isAfter(sentAt.plusMinutes(1)); // 재전송 쿨타임: 1분
  }

  public boolean matchCode(String code) {

    return verificationCode.equals(code);
  }

  public void recordFailedAttempt() {

    this.failedAttemptCount++;

    if (hasExceededMaxAttempts()) {
      invalidate();
    }
  }

  public boolean hasExceededMaxAttempts() {

    return failedAttemptCount >= MAX_FAILED_ATTEMPTS;
  }

  private void invalidate() {

    this.expiresAt = LocalDateTime.now(ZoneOffset.UTC);
  }

  public boolean isVerified() {

    return verified;
  }

  public void verify() {

    this.verified = true;
    this.failedAttemptCount = 0;
  }
}
