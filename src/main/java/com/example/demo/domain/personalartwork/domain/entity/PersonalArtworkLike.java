package com.example.demo.domain.personalartwork.domain.entity;

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
    name = "PersonalArtworkLike",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UQ_PERSONALARTWORKLIKE_ARTWORK_USER",
            columnNames = {"personalArtworkId", "userId"}))
public class PersonalArtworkLike extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalArtworkLikeId")
  private Long id;

  @Column(nullable = false)
  private Long personalArtworkId;

  @Column(nullable = false)
  private Long userId;

  @Column private LocalDateTime deletedAt;

  protected PersonalArtworkLike() {}

  private PersonalArtworkLike(Long personalArtworkId, Long userId) {
    this.personalArtworkId = requirePositive(personalArtworkId, "personalArtworkId");
    this.userId = requirePositive(userId, "userId");
  }

  public static PersonalArtworkLike create(Long personalArtworkId, Long userId) {
    return new PersonalArtworkLike(personalArtworkId, userId);
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
