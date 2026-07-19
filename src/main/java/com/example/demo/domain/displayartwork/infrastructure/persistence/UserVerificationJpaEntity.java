package com.example.demo.domain.displayartwork.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`User`")
public class UserVerificationJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(name = "isVerified")
  private boolean verified;

  protected UserVerificationJpaEntity() {}
}
