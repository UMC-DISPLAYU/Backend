package com.example.demo.domain.artworkcommunication.domain.aggregate;

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
@Table(name = "ArtworkFeelingReply")
public class ArtworkFeelingReply extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingReplyId")
  private Long feelingReplyId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "feelingId", nullable = false)
  private Long feelingId;

  protected ArtworkFeelingReply() {}

  private ArtworkFeelingReply(Long feelingReplyId, String content, Long feelingId, Long userId) {
    this.feelingReplyId = feelingReplyId;
    this.content = content;
    this.feelingId = feelingId;
    this.userId = userId;
  }

  public static ArtworkFeelingReply create(Long feelingId, Long userId, String content) {
    return new ArtworkFeelingReply(null, content, feelingId, userId);
  }

  public boolean belongsToFeeling(Long feelingId) {
    return this.feelingId.equals(feelingId);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }
}
