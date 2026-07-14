package com.example.demo.domain.archive.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "ArchiveWork")
public class ArchiveWork {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "archiveWorkId")
  private Long id;

  @Column(nullable = false)
  private Long displayArtworkId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private LocalDateTime savedAt;

  protected ArchiveWork() {}

  private ArchiveWork(Long displayArtworkId, Long userId) {
    this.displayArtworkId =
        Objects.requireNonNull(displayArtworkId, "displayArtworkId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
    this.savedAt = LocalDateTime.now();
  }

  public static ArchiveWork create(Long displayArtworkId, Long userId) {
    return new ArchiveWork(displayArtworkId, userId);
  }
}
