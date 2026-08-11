package com.example.demo.domain.archive.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DisplayArtwork")
public class ArchiveDisplayArtworkReferenceJpaEntity {

  @Id
  @Column(name = "displayArtworkId")
  private Long displayArtworkId;

  protected ArchiveDisplayArtworkReferenceJpaEntity() {}
}
