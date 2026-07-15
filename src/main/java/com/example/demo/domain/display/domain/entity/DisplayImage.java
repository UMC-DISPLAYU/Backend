package com.example.demo.domain.display.domain.entity;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.DisplayImageType;
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
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayImage")
public class DisplayImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "disImageId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayId", nullable = false)
  private Display display;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisplayImageType imageType;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  private LocalDateTime deletedAt;

  protected DisplayImage() {}

  public DisplayImage(
      Long id,
      String imageUrl,
      DisplayImageType imageType,
      int width,
      int height,
      int sortOrder,
      LocalDateTime deletedAt) {
    this.id = id;
    this.imageUrl = requireNonBlank(imageUrl, "imageUrl");
    this.imageType = Objects.requireNonNull(imageType, "imageType must not be null.");
    this.width = requirePositive(width, "width");
    this.height = requirePositive(height, "height");
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
    this.deletedAt = deletedAt;
  }

  public void assignDisplay(Display display) {
    this.display = Objects.requireNonNull(display, "display must not be null.");
  }

  public void changeSortOrder(int sortOrder) {
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
  }

  public void changeImageUrl(String imageUrl) {
    this.imageUrl = requireNonBlank(imageUrl, "imageUrl");
  }

  public void delete() {
    this.deletedAt = LocalDateTime.now();
  }

  public void restore() {
    this.deletedAt = null;
  }

  public boolean isDeleted() {
    return deletedAt != null;
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
