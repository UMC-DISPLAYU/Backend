package com.example.demo.domain.displaycommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReviewLike")
public class DisplayReviewLike extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewLikeId")
  private Long displayReviewLikeId;

  @Column(name = "displayReviewId", nullable = false)
  private Long displayReviewId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected DisplayReviewLike() {}

  private DisplayReviewLike(Long displayReviewLikeId, Long displayReviewId, Long userId) {
    this.displayReviewLikeId = displayReviewLikeId;
    this.displayReviewId = displayReviewId;
    this.userId = userId;
  }

  public static DisplayReviewLike create(Long displayReviewId, Long userId) {
    return new DisplayReviewLike(null, displayReviewId, userId);
  }
}
