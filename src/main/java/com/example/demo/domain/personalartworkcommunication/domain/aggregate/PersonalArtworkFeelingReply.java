package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.entity.PersonalArtworkFeelingReplyImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "PersonalArtworkFeelingReply")
public class PersonalArtworkFeelingReply extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingReplyId")
  private Long personalFeelingReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "personalFeelingId", nullable = false)
  private Long personalFeelingId;

  @OneToMany(
      mappedBy = "personalArtworkFeelingReply",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<PersonalArtworkFeelingReplyImage> images = new ArrayList<>();

  protected PersonalArtworkFeelingReply() {}

  private PersonalArtworkFeelingReply(
      Long personalFeelingReplyId, String content, Long userId, Long personalFeelingId) {
    this.personalFeelingReplyId = personalFeelingReplyId;
    this.content = content;
    this.personalFeelingId = personalFeelingId;
    this.userId = userId;
  }

  public static PersonalArtworkFeelingReply create(
      Long personalFeelingId, Long userId, String content, List<ImageInfo> images) {
    validateImages(images);
    PersonalArtworkFeelingReply reply =
        new PersonalArtworkFeelingReply(null, content, userId, personalFeelingId);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      reply.images.add(
          new PersonalArtworkFeelingReplyImage(
              reply, image.imageUrl(), image.width(), image.height(), index));
    }
    return reply;
  }

  public List<PersonalArtworkFeelingReplyImage> getImages() {
    return List.copyOf(images);
  }

  public boolean belongsToFeeling(Long personalFeelingId) {
    return this.personalFeelingId.equals(personalFeelingId);
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
