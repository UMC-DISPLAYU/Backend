package com.example.demo.domain.archive.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ArchivePersonalWork")
public class ArchivePersonalWork {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "archivePersonalWorkId")
  private Long id;

  @Column(nullable = false)
  private Long personalArtworkId;

  @Column(nullable = false)
  private Long userId;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime savedAt;

  @Column private LocalDateTime deletedAt;

  protected ArchivePersonalWork() {}

  private ArchivePersonalWork(Long personalArtworkId, Long userId) {
    this.personalArtworkId =
        Objects.requireNonNull(personalArtworkId, "personalArtworkId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  public static ArchivePersonalWork create(Long personalArtworkId, Long userId) {
    return new ArchivePersonalWork(personalArtworkId, userId);
  }

  public void delete() {
    if (deletedAt == null) {
      deletedAt = LocalDateTime.now(ZoneOffset.UTC);
    }
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }
}
