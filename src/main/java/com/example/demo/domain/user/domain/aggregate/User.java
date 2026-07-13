package com.example.demo.domain.user.domain.aggregate;

import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

  @Column(name = "nicknameChangeAt")
  private LocalDateTime nicknameChangeAt;

    public void verifyAuthor(String schoolEmail) {
        this.isVerified = true;
        this.schoolEmail = schoolEmail;
    }
}


