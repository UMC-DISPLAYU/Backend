package com.example.demo.domain.archive.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "DisplayImage")
public class ArchiveDisplayImageReferenceJpaEntity {

  @Id
  @Column(name = "disImageId")
  private Long disImageId;

  @Column(name = "displayId")
  private Long displayId;

  @Column(name = "imageUrl")
  private String imageUrl;

  @Column(name = "imageType")
  private String imageType;

  @Column(name = "sortOrder")
  private Integer sortOrder;

  @Column(name = "deletedAt")
  private LocalDateTime deletedAt;

  protected ArchiveDisplayImageReferenceJpaEntity() {}

  public Long getDisplayId() {
    return displayId;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
