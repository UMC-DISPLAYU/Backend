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
@Table(name = "ArtworkQuestionLike")
public class ArtworkQuestionLike extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "questionLikeId")
  private Long questionLikeId;

  @Column(name = "questionId", nullable = false)
  private Long questionId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected ArtworkQuestionLike() {}

  private ArtworkQuestionLike(Long questionLikeId, Long questionId, Long userId) {
    this.questionLikeId = questionLikeId;
    this.questionId = questionId;
    this.userId = userId;
  }

  public static ArtworkQuestionLike create(Long questionId, Long userId) {
    return new ArtworkQuestionLike(null, questionId, userId);
  }
}
