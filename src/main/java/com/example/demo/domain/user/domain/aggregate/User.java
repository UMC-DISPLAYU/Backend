package com.example.demo.domain.user.domain.aggregate;

import com.example.demo.domain.user.domain.entity.SchoolEmailVerification;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.domain.type.UserRole;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
@Table(
    name = "User",
    uniqueConstraints =
        @UniqueConstraint(name = "UQ_USER_SCHOOL_EMAIL", columnNames = "schoolEmail"))
public class User extends BaseTimeEntity {

  public static final String WITHDRAWN_DISPLAY_NAME = "탈퇴한 회원";
  private static final String WITHDRAWN_IDENTIFIER_PREFIX = "withdrawn_";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userId")
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false)
  private Provider provider;

  @Column(name = "providerId", nullable = false)
  private String providerId;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false)
  private UserRole role = UserRole.USER;

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

  @Column(name = "univName", nullable = true)
  private String univName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "universityId")
  private University university;

  @Column(name = "deletedAt")
  private LocalDateTime deletedAt;

  @Column(name = "nicknameChangeAt")
  private LocalDateTime nicknameChangeAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<SchoolEmailVerification> schoolEmailVerifications = new ArrayList<>();

  public void verifySchoolEmail(String schoolEmail, String univName) {
    verifySchoolEmail(schoolEmail, univName, null);
  }

  public void verifySchoolEmail(String schoolEmail, String univName, University university) {
    if (this.schoolEmail != null) {
      throw new UserException(UserErrorCode.ALREADY_VERIFIED_USER);
    }
    this.schoolEmail = schoolEmail.trim().toLowerCase(Locale.ROOT);
    this.univName = univName;
    this.university = university;
  }

  public void completeArtistVerification() {
    this.isVerified = true;
  }

  public void withdraw(LocalDateTime withdrawnAt) {
    if (deletedAt != null) {
      throw new UserException(UserErrorCode.ALREADY_WITHDRAWN_USER);
    }
    String withdrawnIdentifier = WITHDRAWN_IDENTIFIER_PREFIX + UUID.randomUUID();
    this.providerId = withdrawnIdentifier;
    this.name = WITHDRAWN_DISPLAY_NAME;
    this.nickname = WITHDRAWN_DISPLAY_NAME;
    this.profileImageUrl = null;
    this.isVerified = false;
    this.socialEmail = withdrawnIdentifier + "@withdrawn.invalid";
    this.schoolEmail = null;
    this.univName = null;
    this.university = null;
    this.role = UserRole.USER;
    this.deletedAt = withdrawnAt;
  }

  public void changeNickname(Nickname nickname) {
    this.nickname = nickname.value();
  }

  public void changeProfileImage(String profileImageUrl) {
    this.profileImageUrl = profileImageUrl;
  }

  public void changeUniversity(University university) {
    this.university = university;
    this.univName = university.getName();
  }
}
