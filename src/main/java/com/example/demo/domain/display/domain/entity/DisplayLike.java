package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(
    name = "DisplayLike",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UQ_DISPLAYLIKE_DISPLAY_USER",
            columnNames = {"displayId", "userId"}))
public class DisplayLike extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayLikeId")
  private Long id;

  @Column(nullable = false)
  private Long displayId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId userId;

  @Column private LocalDateTime deletedAt;

  protected DisplayLike() {}

  private DisplayLike(Long id, Long displayId, UserId userId, LocalDateTime deletedAt) {
    this.id = id;
    this.displayId = requirePositive(displayId, "displayId");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
    this.deletedAt = deletedAt;
  }

  public static DisplayLike create(Long displayId, UserId userId) {
    return new DisplayLike(null, displayId, userId, null);
  }

  public void cancel() {
    if (isActive()) {
      this.deletedAt = LocalDateTime.now();
    }
  }

  public void restore() {
    this.deletedAt = null;
  }

  public boolean isActive() {
    return deletedAt == null;
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }

  private static Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
    }
    return value;
  }
}
