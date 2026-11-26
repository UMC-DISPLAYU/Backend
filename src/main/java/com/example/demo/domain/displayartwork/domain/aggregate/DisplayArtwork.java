package com.example.demo.domain.displayartwork.domain.aggregate;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkField;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.type.ArtworkFit;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.DisplayArtworkStatus;
import com.example.demo.domain.displayartwork.domain.vo.ArtworkCrop;
import com.example.demo.domain.displayartwork.domain.vo.ArtworkPart;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.file.domain.MediaType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Table(name = "DisplayArtwork")
public class DisplayArtwork extends SoftDeleteBaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayArtworkId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Column(nullable = false)
  private String artworkName;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ArtworkType type;

  @Column(nullable = false)
  private int productionYear;

  @Column private String materialMedia;

  @Embedded private ArtworkCrop artworkCrop;

  // 운영 마이그레이션의 DEFAULT 'FILL'과 같게 맞춰, 테스트 DB(엔티티로 생성)에서도 값 없이 넣은 행이 FILL이 되게 한다.
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'FILL'")
  @Column(nullable = false, length = 10)
  private ArtworkFit artworkFit = ArtworkFit.FILL;

  private String size;

  @Column(columnDefinition = "TEXT")
  private String point;

  @Column(nullable = false)
  private int workSortOrder;

  @Column(nullable = false)
  private Long registeredByUserId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayArtworkStatus status;

  private LocalDateTime firstPublishedAt;

  @OneToMany(mappedBy = "displayArtwork", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  private final List<ArtworkImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "displayArtwork", cascade = CascadeType.ALL, orphanRemoval = true)
  @BatchSize(size = 50)
  private final List<ArtworkField> fields = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "ArtworkPart", joinColumns = @JoinColumn(name = "displayArtworkId"))
  @OrderColumn(name = "partOrder")
  private final List<ArtworkPart> parts = new ArrayList<>();

  private static final int MAX_FIELDS = 3;

  protected DisplayArtwork() {}

  public static DisplayArtwork create(
      Display display,
      String artworkName,
      String content,
      List<ArtworkType> types,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      int workSortOrder,
      Long registeredByUserId,
      List<ArtworkImage> images) {
    return create(
        display,
        artworkName,
        content,
        types,
        productionYear,
        materialMedia,
        size,
        point,
        workSortOrder,
        registeredByUserId,
        DisplayArtworkStatus.PUBLISHED,
        images,
        List.of());
  }

  public static DisplayArtwork create(
      Display display,
      String artworkName,
      String content,
      List<ArtworkType> types,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      int workSortOrder,
      Long registeredByUserId,
      DisplayArtworkStatus status,
      List<ArtworkImage> images,
      List<ArtworkPart> parts) {
    DisplayArtwork displayArtwork =
        new DisplayArtwork(
            display,
            artworkName,
            content,
            types,
            productionYear,
            materialMedia,
            size,
            point,
            workSortOrder,
            registeredByUserId,
            status);
    displayArtwork.replacePartsAndImages(parts, images, null);
    return displayArtwork;
  }

  private DisplayArtwork(
      Display display,
      String artworkName,
      String content,
      List<ArtworkType> types,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      int workSortOrder,
      Long registeredByUserId,
      DisplayArtworkStatus status) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
    changeContent(artworkName, content, types, productionYear, materialMedia, size, point);
    this.workSortOrder = requireNonNegative(workSortOrder, "workSortOrder");
    this.registeredByUserId =
        Objects.requireNonNull(registeredByUserId, "registeredByUserId must not be null.");
    this.status = Objects.requireNonNull(status, "status must not be null.");
  }

  public List<String> getMediaFileUrls() {
    return images.stream().flatMap(image -> image.media().fileUrls().stream()).distinct().toList();
  }

  public List<ArtworkImage> getImages() {
    return images.stream()
        .sorted(
            Comparator.comparing(
                    ArtworkImage::getPartIndex, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(ArtworkImage::getSortOrder))
        .toList();
  }

  public List<ArtworkPart> getParts() {
    return Collections.unmodifiableList(parts);
  }

  public void changeContent(
      String artworkName,
      String content,
      List<ArtworkType> types,
      int productionYear,
      String materialMedia,
      String size,
      String point) {
    // 작품설명/규격/감상 포인트는 디자인상 선택 항목이라 비어 있어도 허용한다.
    this.artworkName = requireNonBlank(artworkName, "artworkName");
    this.content = content;
    // type은 replaceFields가 첫 번째 분야로 함께 갱신한다.
    replaceFields(types);
    this.productionYear = productionYear;
    this.materialMedia =
        materialMedia == null || materialMedia.isBlank() ? null : materialMedia.trim();
    this.size = size;
    this.point = point;
  }

  public void changeWorkSortOrder(int workSortOrder) {
    this.workSortOrder = requireNonNegative(workSortOrder, "workSortOrder");
  }

  public void publish(LocalDateTime publishedAt) {
    Objects.requireNonNull(publishedAt, "publishedAt must not be null.");
    if (firstPublishedAt == null) firstPublishedAt = publishedAt;
    this.status = DisplayArtworkStatus.PUBLISHED;
  }

  public void addImage(ArtworkImage image) {
    ArtworkImage artworkImage = Objects.requireNonNull(image, "image must not be null.");
    List<ArtworkImage> candidates = new ArrayList<>(images);
    candidates.add(artworkImage);
    validateMedia(candidates, parts.size());
    artworkImage.assignDisplayArtwork(this);
    images.add(artworkImage);
  }

  /**
   * 작품 분야를 통째로 교체한다.
   *
   * <p>{@code type} 컬럼은 아직 여러 조회와 응답이 사용하므로, 첫 번째 분야를 그대로 반영해 둘을 어긋나지 않게 유지한다.
   */
  public void replaceFields(List<ArtworkType> newFields) {
    List<ArtworkType> distinct = requireOneToThreeFields(newFields);
    if (distinct.stream()
        .anyMatch(
            field -> !field.isSelectable() && !getFieldTypes().contains(field) && type != field)) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_FIELD);
    }
    // 전체를 지우고 다시 넣으면, 유지되는 분야가 같은 값으로 다시 INSERT되어 유니크 제약에 걸린다.
    // (Hibernate가 고아 삭제보다 INSERT를 먼저 내보내는 경우가 있다) 그래서 차이만 반영한다.
    fields.removeIf(existing -> !distinct.contains(existing.getField()));
    distinct.stream()
        .filter(field -> fields.stream().noneMatch(existing -> existing.getField() == field))
        .forEach(
            field -> {
              ArtworkField added = new ArtworkField(field);
              added.assignDisplayArtwork(this);
              fields.add(added);
            });
    this.type = distinct.getFirst();
  }

  public List<ArtworkType> getFieldTypes() {
    return fields.stream().map(field -> field.getField().canonical()).distinct().toList();
  }

  private static List<ArtworkType> requireOneToThreeFields(List<ArtworkType> newFields) {
    if (newFields == null || newFields.isEmpty() || newFields.stream().anyMatch(Objects::isNull)) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_FIELD_COUNT);
    }
    // 같은 분야를 두 번 보내는 것은 1개 지정과 같으므로 중복을 제거한 뒤 개수를 따진다.
    List<ArtworkType> distinct = newFields.stream().map(ArtworkType::canonical).distinct().toList();
    if (distinct.size() > MAX_FIELDS) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_FIELD_COUNT);
    }
    return distinct;
  }

  public void cropRepresentativeImage(ArtworkCrop crop) {
    if (crop != null) {
      this.artworkCrop = crop;
    }
  }

  /** 대표 사진 표시 방식을 바꾼다. 값을 보내지 않은 요청(null)이면 기존 값을 유지한다. */
  public void fitRepresentativeImage(ArtworkFit fit) {
    if (fit != null) {
      this.artworkFit = fit;
    }
  }

  public void replaceImages(List<ArtworkImage> newImages) {
    replacePartsAndImages(null, newImages, null);
  }

  public void replacePartsAndImages(
      List<ArtworkPart> newParts, List<ArtworkImage> newImages, ArtworkCrop requestedCrop) {
    List<ArtworkPart> finalParts = newParts == null ? parts : List.copyOf(newParts);
    List<ArtworkImage> replacement = List.copyOf(newImages);
    validateMedia(replacement, finalParts.size());
    boolean reclassifiedVideo =
        replacement.stream()
            .filter(image -> image.getMediaType() == MediaType.IMAGE)
            .anyMatch(
                image ->
                    images.stream()
                        .anyMatch(
                            previous ->
                                previous.getMediaType() == MediaType.VIDEO
                                    && previous.getImageUrl().equals(image.getImageUrl())));
    if (reclassifiedVideo) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_MEDIA);
    }
    String previousCardImageUrl = representativeImageUrl();
    if (newParts != null) {
      parts.clear();
      parts.addAll(finalParts);
    }
    images.clear();
    replacement.forEach(image -> image.assignDisplayArtwork(this));
    images.addAll(replacement);
    if (requestedCrop != null) {
      artworkCrop = requestedCrop;
    } else if (!Objects.equals(previousCardImageUrl, representativeImageUrl())) {
      artworkCrop = null;
    }
  }

  public ArtworkImage representativeImage() {
    return images.stream()
        .filter(ArtworkImage::belongsToFirstPart)
        .min(
            Comparator.comparing(ArtworkImage::isThumbnail)
                .reversed()
                .thenComparingInt(ArtworkImage::getSortOrder))
        .orElse(null);
  }

  private String representativeImageUrl() {
    ArtworkImage image = representativeImage();
    return image == null ? null : image.cardImageUrl();
  }

  private static void validateMedia(List<ArtworkImage> images, int partCount) {
    int artworkPhotoCount = 0;
    boolean firstPartHasMedia = false;
    Set<Integer> additionalPartsWithPhoto = new HashSet<>();
    for (ArtworkImage image : images) {
      Integer partIndex = image.getPartIndex();
      if (image.isThumbnail() && !image.belongsToFirstPart()) {
        throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_THUMBNAIL);
      }
      if (image.getImageType() == ArtworkImageType.WORK_PROCESS) {
        if (partIndex != null) {
          throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_PART_INDEX);
        }
        if (image.getMediaType() == MediaType.VIDEO) {
          throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_MEDIA);
        }
        continue;
      }
      if (partIndex < 0 || partIndex > partCount) {
        throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_PART_INDEX);
      }
      if (image.getMediaType() == MediaType.IMAGE) {
        artworkPhotoCount++;
      }
      if (partIndex == 0) {
        firstPartHasMedia = true;
      } else if (image.getMediaType() == MediaType.IMAGE) {
        additionalPartsWithPhoto.add(partIndex);
      }
    }
    if (artworkPhotoCount > 10) {
      throw new BusinessException(DisplayArtworkErrorCode.INVALID_ARTWORK_MEDIA);
    }
    if (!firstPartHasMedia) {
      throw new BusinessException(DisplayArtworkErrorCode.AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED);
    }
    if (additionalPartsWithPhoto.size() != partCount) {
      throw new BusinessException(DisplayArtworkErrorCode.ARTWORK_PART_PHOTO_REQUIRED);
    }
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }

  private static int requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must not be negative.");
    }
    return value;
  }
}
