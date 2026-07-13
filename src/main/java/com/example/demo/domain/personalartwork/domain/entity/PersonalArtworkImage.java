package com.example.demo.domain.personalartwork.domain.entity;

import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
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
@Table(name = "PersonalArtworkImage")
public class PersonalArtworkImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalArtworkImageId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalArtworkId", nullable = false)
  private PersonalArtwork personalArtwork;

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

  protected PersonalArtworkImage() {}

  public PersonalArtworkImage(
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

  public void assignPersonalArtwork(PersonalArtwork personalArtwork) {
    this.personalArtwork =
        Objects.requireNonNull(personalArtwork, "personalArtwork must not be null.");
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
