package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "PersonalArtwork")
public class PersonalArtworkReferenceJpaEntity {

  @Id
  @Column(name = "personalArtworkId")
  private Long personalArtworkId;

  @Column(name = "userId")
  private Long userId;

  @Column(name = "deletedAt")
  private LocalDateTime deletedAt;

  protected PersonalArtworkReferenceJpaEntity() {}
}
