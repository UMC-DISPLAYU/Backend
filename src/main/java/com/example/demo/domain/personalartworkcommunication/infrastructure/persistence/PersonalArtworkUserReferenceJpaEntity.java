package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "`User`")
public class PersonalArtworkUserReferenceJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  @Column(name = "nickname")
  private String nickname;

  @Column(name = "profileImageUrl", length = 2048)
  private String profileImageUrl;

  protected PersonalArtworkUserReferenceJpaEntity() {}
}
