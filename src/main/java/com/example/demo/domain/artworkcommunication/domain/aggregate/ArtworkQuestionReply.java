package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.domain.artworkcommunication.domain.entity.ArtworkQuestionReplyImage;
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
@Table(name = "ArtworkQuestionReply")
public class ArtworkQuestionReply extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "queReplyId")
  private Long queReplyId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "questionId", nullable = false)
  private Long questionId;

  @Column(name = "creatorId")
  private Long creatorId;

  @OneToMany(mappedBy = "artworkQuestionReply", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<ArtworkQuestionReplyImage> images = new ArrayList<>();

  protected ArtworkQuestionReply() {}

  private ArtworkQuestionReply(Long queReplyId, String content, Long questionId, Long creatorId) {
    this.queReplyId = queReplyId;
    this.content = content;
    this.questionId = questionId;
    this.creatorId = creatorId;
  }

  public static ArtworkQuestionReply create(
      Long questionId, String content, Long creatorId, List<ImageInfo> images) {
    validateImages(images);
    ArtworkQuestionReply reply = new ArtworkQuestionReply(null, content, questionId, creatorId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      reply.images.add(
          new ArtworkQuestionReplyImage(
              reply, image.imageUrl(), image.width(), image.height(), index));
    }
    return reply;
  }

  public List<ArtworkQuestionReplyImage> getImages() {
    return List.copyOf(images);
  }

  public boolean belongsToQuestion(Long questionId) {
    return this.questionId.equals(questionId);
  }

  public boolean isWrittenBy(Long creatorId) {
    return this.creatorId.equals(creatorId);
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
