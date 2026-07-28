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
@Table(name = "ArtworkFeelingReplyLike")
public class ArtworkFeelingReplyLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingReplyLikeId")
  private Long feelingReplyLikeId;

  @Column(name = "feelingReplyId", nullable = false)
  private Long feelingReplyId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected ArtworkFeelingReplyLike() {}

  private ArtworkFeelingReplyLike(Long feelingReplyLikeId, Long feelingReplyId, Long userId) {
    this.feelingReplyLikeId = feelingReplyLikeId;
    this.feelingReplyId = feelingReplyId;
    this.userId = userId;
  }

  public static ArtworkFeelingReplyLike create(Long feelingReplyId, Long userId) {
    return new ArtworkFeelingReplyLike(null, feelingReplyId, userId);
  }
}
