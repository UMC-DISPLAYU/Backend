package com.example.demo.domain.artworkcommunication.domain.aggregate;

import com.example.demo.domain.artworkcommunication.domain.entity.ArtworkFeelingImage;
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

@Getter
@Entity
@Table(name = "ArtworkFeeling")
public class ArtworkFeeling extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;
  private static final int MAX_IMAGE_URL_LENGTH = 2048;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingId")
  private Long feelingId;

  @Column(name = "displayArtworkId", nullable = false)
  private Long displayArtworkId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @OneToMany(mappedBy = "artworkFeeling", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private final List<ArtworkFeelingImage> images = new ArrayList<>();

  protected ArtworkFeeling() {}

  private ArtworkFeeling(Long feelingId, Long displayArtworkId, Long userId, String content) {
    this.feelingId = feelingId;
    this.displayArtworkId = displayArtworkId;
    this.userId = userId;
    this.content = content;
  }

  public static ArtworkFeeling create(
      Long displayArtworkId, Long userId, String content, List<ImageInfo> images) {
    validateImages(images);
    ArtworkFeeling feeling = new ArtworkFeeling(null, displayArtworkId, userId, content);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      feeling.images.add(
          new ArtworkFeelingImage(feeling, image.imageUrl(), image.width(), image.height(), index));
    }
    return feeling;
  }

  public List<ArtworkFeelingImage> getImages() {
    return List.copyOf(images);
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long displayArtworkId) {
    return this.displayArtworkId.equals(displayArtworkId);
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
