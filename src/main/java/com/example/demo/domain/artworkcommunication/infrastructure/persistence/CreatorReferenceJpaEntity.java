package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Creator")
public class CreatorReferenceJpaEntity {

  @Id
  @Column(name = "creatorId")
  private Long creatorId;

  @Column(name = "creatorName")
  private String creatorName;

  @Column(name = "userId")
  private Long userId;

  @Column(name = "displayArtworkId")
  private Long displayArtworkId;

  @Column(name = "isContact")
  private Boolean isContact;

  protected CreatorReferenceJpaEntity() {}
}
