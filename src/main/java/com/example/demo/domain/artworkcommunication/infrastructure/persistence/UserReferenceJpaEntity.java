package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "`User`")
public class UserReferenceJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(name = "nickname")
  private String nickname;

  protected UserReferenceJpaEntity() {}
}
