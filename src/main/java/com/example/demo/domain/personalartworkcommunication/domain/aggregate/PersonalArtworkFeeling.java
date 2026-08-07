package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.entity.PersonalArtworkFeelingImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "PersonalArtworkFeeling")
public class PersonalArtworkFeeling extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingId")
  private Long personalFeelingId;

  @Column(name = "personalArtworkId", nullable = false)
  private Long personalArtworkId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @OneToMany(mappedBy = "personalArtworkFeeling", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<PersonalArtworkFeelingImage> images = new ArrayList<>();

  protected PersonalArtworkFeeling() {}

  private PersonalArtworkFeeling(
      Long personalFeelingId, Long personalArtworkId, Long userId, String content) {
    this.personalFeelingId = personalFeelingId;
    this.personalArtworkId = personalArtworkId;
    this.userId = userId;
    this.content = content;
  }

  public static PersonalArtworkFeeling create(
      Long personalArtworkId, Long userId, String content, List<ImageInfo> images) {
    validateImages(images);
    PersonalArtworkFeeling feeling =
        new PersonalArtworkFeeling(null, personalArtworkId, userId, content);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      feeling.images.add(
          new PersonalArtworkFeelingImage(
              feeling, image.imageUrl(), image.width(), image.height(), index));
    }
    return feeling;
  }

  public List<PersonalArtworkFeelingImage> getImages() {
    return List.copyOf(images);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long personalArtworkId) {
    return this.personalArtworkId.equals(personalArtworkId);
  }

  private static void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("감상평 이미지는 최대 5개까지 등록할 수 있습니다.");
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
      throw new IllegalArgumentException("유효하지 않은 감상평 이미지입니다.");
    }
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
