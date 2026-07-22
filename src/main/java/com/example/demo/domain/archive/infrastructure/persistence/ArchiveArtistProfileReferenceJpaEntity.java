package com.example.demo.domain.archive.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ArtistProfile")
public class ArchiveArtistProfileReferenceJpaEntity {

  @Id
  @Column(name = "artistProfileId")
  private Long artistProfileId;

  protected ArchiveArtistProfileReferenceJpaEntity() {}
}
