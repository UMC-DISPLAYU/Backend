package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PersonalArtworkFeelingLike")
public class PersonalArtworkFeelingLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingLikeId")
  private Long personalFeelingLikeId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "personalFeelingId", nullable = false)
  private Long personalFeelingId;

  protected PersonalArtworkFeelingLike() {}

  private PersonalArtworkFeelingLike(
      Long personalFeelingLikeId, Long personalFeelingId, Long userId) {
    this.personalFeelingLikeId = personalFeelingLikeId;
    this.personalFeelingId = personalFeelingId;
    this.userId = userId;
  }

  public static PersonalArtworkFeelingLike create(Long personalFeelingId, Long userId) {
    return new PersonalArtworkFeelingLike(null, personalFeelingId, userId);
  }
}
