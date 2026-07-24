package com.example.demo.domain.displaycommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReviewReply")
public class DisplayReviewReply extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewReplyId")
  private Long displayReviewReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "displayReviewId", nullable = false)
  private Long displayReviewId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected DisplayReviewReply() {}

  private DisplayReviewReply(Long displayReviewId, Long userId, String content) {
    this.displayReviewId = displayReviewId;
    this.userId = userId;
    this.content = content;
  }

  public static DisplayReviewReply create(Long displayReviewId, Long userId, String content) {
    return new DisplayReviewReply(displayReviewId, userId, content);
  }

  public boolean belongsToReview(Long displayReviewId) {
    return this.displayReviewId.equals(displayReviewId);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }
}
