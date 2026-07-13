package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`User`")
public class UserReferenceJpaEntity {

  @Id
  @Column(name = "userId")
  private Long userId;

  protected UserReferenceJpaEntity() {}
}
