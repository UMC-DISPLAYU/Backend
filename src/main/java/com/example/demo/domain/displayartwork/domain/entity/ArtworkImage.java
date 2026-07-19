package com.example.demo.domain.displayartwork.domain.entity;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.global.entity.BaseTimeEntity;
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
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "ArtworkImage")
public class ArtworkImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "artImageId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayArtworkId", nullable = false)
  private DisplayArtwork displayArtwork;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private boolean isThumbnail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ArtworkImageType imageType;

  @Column(nullable = false)
  private int sortOrder;

  private String caption;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  protected ArtworkImage() {}

  public ArtworkImage(
      Long id,
      String imageUrl,
      boolean isThumbnail,
      ArtworkImageType imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {
    this.id = id;
    this.imageUrl = requireNonBlank(imageUrl, "imageUrl");
    this.isThumbnail = isThumbnail;
    this.imageType = Objects.requireNonNull(imageType, "imageType must not be null.");
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
    this.caption = caption;
    this.width = requirePositive(width, "width");
    this.height = requirePositive(height, "height");
  }

  public void assignDisplayArtwork(DisplayArtwork displayArtwork) {
    this.displayArtwork =
        Objects.requireNonNull(displayArtwork, "displayArtwork must not be null.");
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }

  private static int requirePositive(int value, String fieldName) {
    if (value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
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
