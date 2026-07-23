package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import com.example.demo.global.error.BusinessException;
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
@Table(name = "PersonalArtworkQuestion")
public class PersonalArtworkQuestion extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionId")
  private Long personalQuestionId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "isPublic", nullable = false)
  private Boolean isPublic;

  @Enumerated(EnumType.STRING)
  @Column(name = "answerStatus", nullable = false)
  private AnswerStatus answerStatus;

  @Column(name = "personalArtworkId", nullable = false)
  private Long personalArtworkId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  protected PersonalArtworkQuestion() {}

  private PersonalArtworkQuestion(
      Long personalQuestionId,
      String content,
      Boolean isPublic,
      AnswerStatus answerStatus,
      Long personalArtworkId,
      Long userId) {
    this.personalQuestionId = personalQuestionId;
    this.content = content;
    this.isPublic = isPublic;
    this.answerStatus = answerStatus;
    this.personalArtworkId = personalArtworkId;
    this.userId = userId;
  }

  public static PersonalArtworkQuestion create(
      Long personalArtworkId, Long userId, String content, boolean isPublic) {
    return new PersonalArtworkQuestion(
        null, content, isPublic, AnswerStatus.WAITING, personalArtworkId, userId);
  }

  public PersonalArtworkQuestionReply answer(Long userId, String content) {
    if (isAnswered()) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_ALREADY_ANSWERED);
    }

    this.answerStatus = AnswerStatus.ANSWERED;
    return PersonalArtworkQuestionReply.create(this.personalQuestionId, userId, content);
  }

  public boolean isAnswered() {
    return this.answerStatus == AnswerStatus.ANSWERED;
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long personalArtworkId) {
    return this.personalArtworkId.equals(personalArtworkId);
  }
}
