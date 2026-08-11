package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.domain.artworkcommunication.domain.entity.ArtworkQuestionImage;
import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
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
@Table(name = "ArtworkQuestion")
public class ArtworkQuestion extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "questionId")
  private Long questionId;

  @Column(name = "content", nullable = false, length = 300)
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

  @OneToMany(mappedBy = "artworkQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<ArtworkQuestionImage> images = new ArrayList<>();

  protected ArtworkQuestion() {}

  private ArtworkQuestion(
      Long questionId,
      String content,
      Boolean isPublic,
      AnswerStatus answerStatus,
      Long displayArtworkId,
      Long userId) {
    this.questionId = questionId;
    this.content = content;
    this.isPublic = isPublic;
    this.answerStatus = answerStatus;
    this.displayArtworkId = displayArtworkId;
    this.userId = userId;
  }

  public static ArtworkQuestion create(
      Long displayArtworkId,
      Long userId,
      String content,
      Boolean isPublic,
      List<ImageInfo> images) {
    validateImages(images);
    ArtworkQuestion question =
        new ArtworkQuestion(
            null, content, isPublic, AnswerStatus.WAITING, displayArtworkId, userId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      question.images.add(
          new ArtworkQuestionImage(
              question, image.imageUrl(), image.width(), image.height(), index));
    }
    return question;
  }

  public List<ArtworkQuestionImage> getImages() {
    return List.copyOf(images);
  }

  public void update(String content, Boolean isPublic) {
    this.content = content;
    this.isPublic = isPublic;
  }

  public void markAnswered() {
    this.answerStatus = AnswerStatus.ANSWERED;
  }

  public void markWaiting() {
    this.answerStatus = AnswerStatus.WAITING;
  }

  public boolean isAnswered() {
    return AnswerStatus.ANSWERED.equals(this.answerStatus);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long displayArtworkId) {
    return this.displayArtworkId.equals(displayArtworkId);
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
