package com.example.demo.domain.user.domain.aggregate;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "User")
public class User extends BaseTimeEntity {

  private static final long NICKNAME_CHANGE_INTERVAL_DAYS = 30;
  private static final String WITHDRAWN_NICKNAME_PREFIX = "deleted_";

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

  @Column(name = "profileImageUrl", length = 2048)
  private String profileImageUrl;

  @Builder.Default
  @Column(name = "isVerified", nullable = false)
  private boolean isVerified = false; // 작가 인증여부

  @Column(name = "socialEmail", nullable = false)
  private String socialEmail;

  @Column(name = "schoolEmail")
  private String schoolEmail;

  @Column(name = "univ_name", nullable = true)
  private String univName;

  @Column(name = "deletedAt")
  private LocalDateTime deletedAt;

  @Column(name = "nicknameChangeAt")
  private LocalDateTime nicknameChangeAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<SchoolEmailVerification> schoolEmailVerifications = new ArrayList<>();

  public void verifyAuthor(String schoolEmail, String univName) {
    this.isVerified = true;
    this.schoolEmail = schoolEmail;
    this.univName = univName;
  }

  public void withdraw(LocalDateTime withdrawnAt) {
    if (deletedAt != null) {
      throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
    }
    this.nickname = WITHDRAWN_NICKNAME_PREFIX + UUID.randomUUID(); // 탈퇴한 사용자의 닉네임 사용이 가능하도록 허용
    this.deletedAt = withdrawnAt;
  }

  public LocalDateTime changeNickname(Nickname nickname, LocalDateTime changedAt) {
    LocalDateTime nextChangeAvailableAt = getNextNicknameChangeAvailableAt();
    if (nextChangeAvailableAt != null && changedAt.isBefore(nextChangeAvailableAt)) {
      throw new UserException(UserErrorCode.NICKNAME_CHANGE_NOT_ALLOWED);
    }
    this.nickname = nickname.value();
    this.nicknameChangeAt = changedAt;
    return changedAt.plusDays(NICKNAME_CHANGE_INTERVAL_DAYS);
  }

  public LocalDateTime getNextNicknameChangeAvailableAt() {
    return nicknameChangeAt == null
        ? null
        : nicknameChangeAt.plusDays(NICKNAME_CHANGE_INTERVAL_DAYS);
  }

  public void changeProfileImage(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void changeUnivName(String univName) {
    this.univName = univName;
  }
}
