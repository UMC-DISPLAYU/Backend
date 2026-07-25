package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

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
@Table(name = "PersonalArtworkFeelingReplyLike")
public class PersonalArtworkFeelingReplyLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingReplyLikeId")
  private Long personalFeelingReplyLikeId;

  @Column(name = "personalFeelingReplyId", nullable = false)
  private Long personalFeelingReplyId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected PersonalArtworkFeelingReplyLike() {}

  private PersonalArtworkFeelingReplyLike(
      Long personalFeelingReplyLikeId, Long personalFeelingReplyId, Long userId) {
    this.personalFeelingReplyLikeId = personalFeelingReplyLikeId;
    this.personalFeelingReplyId = personalFeelingReplyId;
    this.userId = userId;
  }

  public static PersonalArtworkFeelingReplyLike create(Long personalFeelingReplyId, Long userId) {
    return new PersonalArtworkFeelingReplyLike(null, personalFeelingReplyId, userId);
  }
}
