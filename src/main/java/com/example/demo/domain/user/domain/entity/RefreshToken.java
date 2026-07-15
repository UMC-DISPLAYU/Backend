package com.example.demo.domain.user.domain.entity;

import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RefreshToken")
public class RefreshToken extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tokenId")
  private Long id;

  @Column(name = "refreshToken", nullable = false)
  private String refreshToken;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false, unique = true)
  private User user;
}
