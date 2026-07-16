package com.example.demo.domain.archive.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
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

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime savedAt;

  protected ArchiveArtist() {}

  private ArchiveArtist(Long creatorId, Long userId) {
    this.creatorId = Objects.requireNonNull(creatorId, "creatorId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  public static ArchiveArtist create(Long creatorId, Long userId) {
    return new ArchiveArtist(creatorId, userId);
  }
}
