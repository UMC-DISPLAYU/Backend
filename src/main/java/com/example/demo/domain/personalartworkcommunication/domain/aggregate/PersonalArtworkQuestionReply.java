package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.entity.PersonalArtworkQuestionReplyImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "PersonalArtworkQuestionReply")
public class PersonalArtworkQuestionReply extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionReplyId")
  private Long personalQuestionReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "personalQuestionId", nullable = false)
  private Long personalQuestionId;

  @OneToMany(
      mappedBy = "personalArtworkQuestionReply",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<PersonalArtworkQuestionReplyImage> images = new ArrayList<>();

  protected PersonalArtworkQuestionReply() {}

  private PersonalArtworkQuestionReply(
      Long personalQuestionReplyId, String content, Long userId, Long personalQuestionId) {
    this.personalQuestionReplyId = personalQuestionReplyId;
    this.content = content;
    this.personalQuestionId = personalQuestionId;
    this.userId = userId;
  }

  public static PersonalArtworkQuestionReply create(
      Long personalQuestionId, Long userId, String content, List<ImageInfo> images) {
    validateImages(images);
    PersonalArtworkQuestionReply reply =
        new PersonalArtworkQuestionReply(null, content, userId, personalQuestionId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      reply.images.add(
          new PersonalArtworkQuestionReplyImage(
              reply, image.imageUrl(), image.width(), image.height(), index));
    }
    return reply;
  }

  public List<PersonalArtworkQuestionReplyImage> getImages() {
    return List.copyOf(images);
  }

  public boolean belongsToQuestion(Long personalQuestionId) {
    return this.personalQuestionId.equals(personalQuestionId);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  private static void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("질문 답변 이미지는 최대 5개까지 등록할 수 있습니다.");
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
      throw new IllegalArgumentException("유효하지 않은 질문 답변 이미지입니다.");
    }
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
