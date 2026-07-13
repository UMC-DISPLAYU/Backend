package com.example.demo.domain.display.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "`Column`")
public class DuPickColumnJpaEntity {

  @Id
  @Column(name = "columnId")
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "columnImageUrl", nullable = false, columnDefinition = "TEXT")
  private String columnImageUrl;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  protected DuPickColumnJpaEntity() {}

  public DuPickColumnJpaEntity(
      Long id,
      String name,
      String content,
      String columnImageUrl,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.name = name;
    this.content = content;
    this.columnImageUrl = columnImageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
