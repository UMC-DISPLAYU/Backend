package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.aggregate.User;
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
@Table(name = "UserAgreement")
public class UserAgreement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userAgreeId")
  private Long id;

  @Builder.Default
  @Column(name = "isAgreed", nullable = false)
  private Boolean isAgreed = false;

  @Column(name = "agreedAt", nullable = false)
  private LocalDateTime agreedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "agreeId", nullable = false)
  private Agreement agreement;
}
