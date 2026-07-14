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
@Table(name = "ArchiveDisplay")
public class ArchiveDisplay {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "archiveDisplayId")
  private Long id;

  @Column(nullable = false)
  private Long displayId;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private LocalDateTime savedAt;

  protected ArchiveDisplay() {}

  private ArchiveDisplay(Long displayId, Long userId) {
    this.displayId = Objects.requireNonNull(displayId, "displayId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
    this.savedAt = LocalDateTime.now();
  }

  public static ArchiveDisplay create(Long displayId, Long userId) {
    return new ArchiveDisplay(displayId, userId);
  }
}
