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

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime savedAt;

  protected ArchiveDisplay() {}

  private ArchiveDisplay(Long displayId, Long userId) {
    this.displayId = Objects.requireNonNull(displayId, "displayId must not be null.");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  public static ArchiveDisplay create(Long displayId, Long userId) {
    return new ArchiveDisplay(displayId, userId);
  }
}
