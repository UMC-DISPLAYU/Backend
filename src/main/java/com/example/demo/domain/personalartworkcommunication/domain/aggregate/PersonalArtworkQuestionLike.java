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
@Table(name = "PersonalArtworkQuestionLike")
public class PersonalArtworkQuestionLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionLikeId")
  private Long personalQuestionLikeId;

  @Column(name = "personalQuestionId", nullable = false)
  private Long personalQuestionId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected PersonalArtworkQuestionLike() {}
}
