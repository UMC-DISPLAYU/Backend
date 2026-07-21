package com.example.demo.domain.personalartworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "User")
public class PersonalArtworkUserReferenceJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  protected PersonalArtworkUserReferenceJpaEntity() {}
}
