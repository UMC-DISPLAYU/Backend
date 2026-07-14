package com.example.demo.domain.personalartwork.domain.aggregate;

import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.error.PersonalArtworkErrorCode;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PersonalArtwork")
public class PersonalArtwork extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalArtworkId")
  private Long id;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId ownerUserId;

  @Column(nullable = false)
  private String artworkName;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ArtworkType type;

  @Column(nullable = false)
  private int productionYear;

  @Column(nullable = false)
  private String materialMedia;

  private String size;

  @Column(columnDefinition = "TEXT")
  private String point;

  @OneToMany(mappedBy = "personalArtwork", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private final List<PersonalArtworkImage> images = new ArrayList<>();

  protected PersonalArtwork() {}

  public static PersonalArtwork create(
      UserId ownerUserId,
      String artworkName,
      String content,
      ArtworkType type,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      List<PersonalArtworkImage> images) {
    PersonalArtwork personalArtwork =
        new PersonalArtwork(
            ownerUserId, artworkName, content, type, productionYear, materialMedia, size, point);
    personalArtwork.replaceImages(images);
    return personalArtwork;
  }

  private PersonalArtwork(
      UserId ownerUserId,
      String artworkName,
      String content,
      ArtworkType type,
      int productionYear,
      String materialMedia,
      String size,
      String point) {
    this.ownerUserId = Objects.requireNonNull(ownerUserId, "ownerUserId must not be null.");
    changeContent(artworkName, content, type, productionYear, materialMedia, size, point);
  }

  // 이미지 목록을 외부에서 직접 수정하지 못하도록 읽기 전용으로 반환한다.
  public List<PersonalArtworkImage> getImages() {
    return Collections.unmodifiableList(images);
  }

  public void changeContent(
      String artworkName,
      String content,
      ArtworkType type,
      int productionYear,
      String materialMedia,
      String size,
      String point) {
    this.artworkName = requireNonBlank(artworkName, "artworkName");
    this.content = content;
    this.type = Objects.requireNonNull(type, "type must not be null.");
    this.productionYear = productionYear;
    this.materialMedia = requireNonBlank(materialMedia, "materialMedia");
    this.size = size;
    this.point = point;
  }

  public void addImage(PersonalArtworkImage image) {
    PersonalArtworkImage personalArtworkImage =
        Objects.requireNonNull(image, "image must not be null.");
    personalArtworkImage.assignPersonalArtwork(this);
    images.add(personalArtworkImage);
  }

  // PATCH 시 이미지 목록 전체를 교체한다 (기존 이미지는 orphanRemoval로 정리됨).
  public void replaceImages(List<PersonalArtworkImage> newImages) {
    requireAtLeastOneArtworkImage(newImages);
    images.clear();
    newImages.forEach(this::addImage);
  }

  public boolean isOwnedBy(Long userId) {
    return userId != null && this.ownerUserId.value().equals(userId);
  }

  private static void requireAtLeastOneArtworkImage(List<PersonalArtworkImage> images) {
    boolean hasArtworkImage =
        images != null
            && images.stream().anyMatch(image -> image.getImageType() == ArtworkImageType.ARTWORK);
    if (!hasArtworkImage) {
      throw new BusinessException(PersonalArtworkErrorCode.AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED);
    }
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
