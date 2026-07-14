package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "ArtworkQuestion")
public class ArtworkQuestion extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "artQueId")
  private Long artQueId;

  @Column(name = "content", nullable = false)
  private String content;

  @Column(name = "isPublic", nullable = false)
  private Boolean isPublic;

  @Enumerated(EnumType.STRING)
  @Column(name = "answerStatus", nullable = false)
  private AnswerStatus answerStatus;

  @Column(name = "displayArtworkId", nullable = false)
  private Long displayArtworkId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected ArtworkQuestion() {}

  private ArtworkQuestion(
      Long artQueId,
      String content,
      Boolean isPublic,
      AnswerStatus answerStatus,
      Long displayArtworkId,
      Long userId) {
    this.artQueId = artQueId;
    this.content = content;
    this.isPublic = isPublic;
    this.answerStatus = answerStatus;
    this.displayArtworkId = displayArtworkId;
    this.userId = userId;
  }

  public static ArtworkQuestion create(
      Long displayArtworkId, Long userId, String content, Boolean isPublic) {
    return new ArtworkQuestion(
        null, content, isPublic, AnswerStatus.WAITING, displayArtworkId, userId);
  }

  public void update(String content, Boolean isPublic) {
    this.content = content;
    this.isPublic = isPublic;
  }

  public void markAnswered() {
    this.answerStatus = AnswerStatus.ANSWERED;
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long displayArtworkId) {
    return this.displayArtworkId.equals(displayArtworkId);
  }
}
