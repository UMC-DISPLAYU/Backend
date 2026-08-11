package com.example.demo.domain.archive.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Display")
public class ArchiveDisplayReferenceJpaEntity {

  @Id
  @Column(name = "displayId")
  private Long displayId;

  protected ArchiveDisplayReferenceJpaEntity() {}
}
