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
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "LoungeCommentLike")
@EntityListeners(AuditingEntityListener.class)
public class LoungeCommentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungeCommentLikeId")
  private Long id;

  @Column(nullable = false)
  private Long loungeCommentId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId userId;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  protected LoungeCommentLike() {}

  public static LoungeCommentLike create(Long loungeCommentId, UserId userId) {
    return new LoungeCommentLike(null, loungeCommentId, userId);
  }

  public LoungeCommentLike(Long id, Long loungeCommentId, UserId userId) {
    this.id = id;
    this.loungeCommentId = requirePositive(loungeCommentId, "loungeCommentId");
    this.userId = Objects.requireNonNull(userId, "userId must not be null.");
  }

  private static Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
    }
    return value;
  }
}
