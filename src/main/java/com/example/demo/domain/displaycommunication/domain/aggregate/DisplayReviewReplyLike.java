package com.example.demo.domain.displaycommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReviewReplyLike")
public class DisplayReviewReplyLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewReplyLikeId")
  private Long displayReviewReplyLikeId;

  @Column(name = "displayReviewReplyId", nullable = false)
  private Long displayReviewReplyId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected DisplayReviewReplyLike() {}

  private DisplayReviewReplyLike(
      Long displayReviewReplyLikeId, Long displayReviewReplyId, Long userId) {
    this.displayReviewReplyLikeId = displayReviewReplyLikeId;
    this.displayReviewReplyId = displayReviewReplyId;
    this.userId = userId;
  }

  public static DisplayReviewReplyLike create(Long displayReviewReplyId, Long userId) {
    return new DisplayReviewReplyLike(null, displayReviewReplyId, userId);
  }
}
