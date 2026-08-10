package com.example.demo.domain.lounge.domain.entity;

import com.example.demo.domain.lounge.domain.vo.UserId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(
    name = "LoungePostLike",
    uniqueConstraints =
        @UniqueConstraint(
            name = "UK_LOUNGEPOSTLIKE_POST_USER",
            columnNames = {"loungePostId", "userId"}))
@EntityListeners(AuditingEntityListener.class)
public class LoungePostLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungePostLikeId")
  private Long id;

  @Column(nullable = false)
  private Long loungePostId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId userId;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected LoungePostLike() {}

  public static LoungePostLike create(Long loungePostId, UserId userId) {
    return new LoungePostLike(null, loungePostId, userId);
  }

  public LoungePostLike(Long id, Long loungePostId, UserId userId) {
    this.id = id;
    this.loungePostId = requirePositive(loungePostId, "loungePostId");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  private static Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
    }
    return value;
  }
}
