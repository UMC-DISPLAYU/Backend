package com.example.demo.domain.artworkcommunication.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DisplayArtwork")
public class DisplayArtworkReferenceJpaEntity {

  @Id
  @Column(name = "displayArtworkId")
  private Long displayArtworkId;

  protected DisplayArtworkReferenceJpaEntity() {}
}
