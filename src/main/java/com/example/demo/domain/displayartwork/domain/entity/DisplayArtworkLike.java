package com.example.demo.domain.displayartwork.domain.entity;

import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@Entity
@Table(
    name = "DisplayArtworkLike",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UQ_DISPLAYARTWORKLIKE_ARTWORK_USER",
            columnNames = {"displayArtworkId", "userId"}))
public class DisplayArtworkLike extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayArtworkLikeId")
  private Long id;

  @Column(nullable = false)
  private Long displayArtworkId;

  @Column(nullable = false)
  private Long userId;

  @Column private LocalDateTime deletedAt;

  protected DisplayArtworkLike() {}

  private DisplayArtworkLike(Long displayArtworkId, Long userId) {
    this.displayArtworkId = requirePositive(displayArtworkId, "displayArtworkId");
    this.userId = requirePositive(userId, "userId");
  }

  public static DisplayArtworkLike create(Long displayArtworkId, Long userId) {
    return new DisplayArtworkLike(displayArtworkId, userId);
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

  private static Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
    }
    return value;
  }
}
