package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.entity.PersonalArtworkQuestionImage;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.type.AnswerStatus;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "PersonalArtworkQuestion")
public class PersonalArtworkQuestion extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

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

  @OneToMany(mappedBy = "personalArtworkQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<PersonalArtworkQuestionImage> images = new ArrayList<>();

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
      Long personalArtworkId,
      Long userId,
      String content,
      boolean isPublic,
      List<ImageInfo> images) {
    validateImages(images);
    PersonalArtworkQuestion question =
        new PersonalArtworkQuestion(
            null, content, isPublic, AnswerStatus.WAITING, personalArtworkId, userId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      question.images.add(
          new PersonalArtworkQuestionImage(
              question, image.imageUrl(), image.width(), image.height(), index));
    }
    return question;
  }

  public PersonalArtworkQuestionReply answer(
      Long userId, String content, List<PersonalArtworkQuestionReply.ImageInfo> images) {
    if (isAnswered()) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_ALREADY_ANSWERED);
    }

    PersonalArtworkQuestionReply reply =
        PersonalArtworkQuestionReply.create(this.personalQuestionId, userId, content, images);
    this.answerStatus = AnswerStatus.ANSWERED;
    return reply;
  }

  public List<PersonalArtworkQuestionImage> getImages() {
    return List.copyOf(images);
  }

  public boolean isAnswered() {
    return this.answerStatus == AnswerStatus.ANSWERED;
  }

  public void markWaiting() {
    this.answerStatus = AnswerStatus.WAITING;
  }

  @Override
  public void delete() {
    if (isAnswered()) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_ALREADY_ANSWERED);
    }
    super.delete();
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean isPublicQuestion() {
    return Boolean.TRUE.equals(this.isPublic);
  }

  public boolean belongsToArtwork(Long personalArtworkId) {
    return this.personalArtworkId.equals(personalArtworkId);
  }

  private static void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("질문 이미지는 최대 5개까지 등록할 수 있습니다.");
    }
    if (images.stream()
        .anyMatch(
            image ->
                image == null
                    || image.imageUrl() == null
                    || image.imageUrl().isBlank()
                    || image.imageUrl().length() > MAX_IMAGE_URL_LENGTH
                    || image.width() <= 0
                    || image.height() <= 0)) {
      throw new IllegalArgumentException("유효하지 않은 질문 이미지입니다.");
    }
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
