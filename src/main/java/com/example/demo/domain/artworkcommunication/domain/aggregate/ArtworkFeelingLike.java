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
@Table(name = "ArtworkFeelingLike")
public class ArtworkFeelingLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingLikeId")
  private Long feelingLikeId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "feelingId", nullable = false)
  private Long feelingId;

  protected ArtworkFeelingLike() {}

  private ArtworkFeelingLike(Long feelingLikeId, Long feelingId, Long userId) {
    this.feelingLikeId = feelingLikeId;
    this.feelingId = feelingId;
    this.userId = userId;
  }

  public static ArtworkFeelingLike create(Long feelingId, Long userId) {
    return new ArtworkFeelingLike(null, feelingId, userId);
  }
}
