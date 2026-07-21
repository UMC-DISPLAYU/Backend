package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PersonalArtwork")
public class PersonalArtworkReferenceJpaEntity {

  @Id
  @Column(name = "personalArtworkId")
  private Long personalArtworkId;

  @Column(name = "userId")
  private Long userId;

  protected PersonalArtworkReferenceJpaEntity() {}
}
