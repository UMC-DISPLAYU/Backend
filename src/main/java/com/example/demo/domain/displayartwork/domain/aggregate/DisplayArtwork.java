package com.example.demo.domain.displayartwork.domain.aggregate;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import com.example.demo.global.error.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

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

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ArtworkType type;

  @Column(nullable = false)
  private int productionYear;

  @Column(nullable = false)
  private String materialMedia;

  @Column(nullable = false)
  private String size;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String point;

  @Column(nullable = false)
  private int workSortOrder;

  @OneToMany(mappedBy = "displayArtwork", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private final List<ArtworkImage> images = new ArrayList<>();

  protected DisplayArtwork() {}

  public static DisplayArtwork create(
      Display display,
      String artworkName,
      String content,
      ArtworkType type,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      int workSortOrder,
      List<ArtworkImage> images) {
    DisplayArtwork displayArtwork =
        new DisplayArtwork(
            display,
            artworkName,
            content,
            type,
            productionYear,
            materialMedia,
            size,
            point,
            workSortOrder);
    displayArtwork.replaceImages(images);
    return displayArtwork;
  }

  private DisplayArtwork(
      Display display,
      String artworkName,
      String content,
      ArtworkType type,
      int productionYear,
      String materialMedia,
      String size,
      String point,
      int workSortOrder) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
    changeContent(artworkName, content, type, productionYear, materialMedia, size, point);
    this.workSortOrder = requireNonNegative(workSortOrder, "workSortOrder");
  }

  public List<ArtworkImage> getImages() {
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
    this.content = requireNonBlank(content, "content");
    this.type = Objects.requireNonNull(type, "type must not be null.");
    this.productionYear = productionYear;
    this.materialMedia = requireNonBlank(materialMedia, "materialMedia");
    this.size = requireNonBlank(size, "size");
    this.point = requireNonBlank(point, "point");
  }

  public void changeWorkSortOrder(int workSortOrder) {
    this.workSortOrder = requireNonNegative(workSortOrder, "workSortOrder");
  }

  public void addImage(ArtworkImage image) {
    ArtworkImage artworkImage = Objects.requireNonNull(image, "image must not be null.");
    artworkImage.assignDisplayArtwork(this);
    images.add(artworkImage);
  }

  public void replaceImages(List<ArtworkImage> newImages) {
    requireAtLeastOneArtworkImage(newImages);
    images.clear();
    newImages.forEach(this::addImage);
  }

  private static void requireAtLeastOneArtworkImage(List<ArtworkImage> images) {
    boolean hasArtworkImage =
        images != null
            && images.stream().anyMatch(image -> image.getImageType() == ArtworkImageType.ARTWORK);
    if (!hasArtworkImage) {
      throw new BusinessException(DisplayArtworkErrorCode.AT_LEAST_ONE_ARTWORK_IMAGE_REQUIRED);
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
