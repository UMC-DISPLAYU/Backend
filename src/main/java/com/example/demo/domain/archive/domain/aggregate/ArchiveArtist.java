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
@Table(name = "ArchiveArtist")
public class ArchiveArtist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "archiveArtistId")
  private Long id;

  @Column(nullable = false)
  private Long creatorId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private LocalDateTime savedAt;

  protected ArchiveArtist() {}

  private ArchiveArtist(Long creatorId, Long userId) {
    this.creatorId = Objects.requireNonNull(creatorId, "creatorId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
    this.savedAt = LocalDateTime.now();
  }

  public static ArchiveArtist create(Long creatorId, Long userId) {
    return new ArchiveArtist(creatorId, userId);
  }
}
