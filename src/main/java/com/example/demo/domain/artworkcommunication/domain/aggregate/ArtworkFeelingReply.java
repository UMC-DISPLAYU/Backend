package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.domain.artworkcommunication.domain.entity.ArtworkFeelingReplyImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "ArtworkFeelingReply")
public class ArtworkFeelingReply extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingReplyId")
  private Long feelingReplyId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "feelingId", nullable = false)
  private Long feelingId;

  @OneToMany(mappedBy = "artworkFeelingReply", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<ArtworkFeelingReplyImage> images = new ArrayList<>();

  protected ArtworkFeelingReply() {}

  private ArtworkFeelingReply(Long feelingReplyId, String content, Long feelingId, Long userId) {
    this.feelingReplyId = feelingReplyId;
    this.content = content;
    this.feelingId = feelingId;
    this.userId = userId;
  }

  public static ArtworkFeelingReply create(
      Long feelingId, Long userId, String content, List<ImageInfo> images) {
    validateImages(images);
    ArtworkFeelingReply reply = new ArtworkFeelingReply(null, content, feelingId, userId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      reply.images.add(
          new ArtworkFeelingReplyImage(
              reply, image.imageUrl(), image.width(), image.height(), index));
    }
    return reply;
  }

  public List<ArtworkFeelingReplyImage> getImages() {
    return List.copyOf(images);
  }

  public boolean belongsToFeeling(Long feelingId) {
    return this.feelingId.equals(feelingId);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  private static void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("감상평 답변 이미지는 최대 5개까지 등록할 수 있습니다.");
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
      throw new IllegalArgumentException("유효하지 않은 감상평 답변 이미지입니다.");
    }
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
