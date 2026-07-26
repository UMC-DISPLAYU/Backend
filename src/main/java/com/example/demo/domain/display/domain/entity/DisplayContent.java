package com.example.demo.domain.display.domain.entity;

import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "DisplayContent")
public class DisplayContent extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayContentId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoryId", nullable = false)
  private DisplayContentCategory category;

  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(name = "contentsSortOrder", nullable = false)
  private int sortOrder;

  protected DisplayContent() {}

  public DisplayContent(Long id, String imageUrl, int width, int height, int sortOrder) {
    this.id = id;
    changeImageInfo(imageUrl, width, height);
    changeSortOrder(sortOrder);
  }

  public void assignCategory(DisplayContentCategory category) {
    this.category = Objects.requireNonNull(category, "category must not be null.");
  }

  public void changeSortOrder(int sortOrder) {
    this.sortOrder = requireSortOrder(sortOrder);
  }

  public void changeImageInfo(String imageUrl, int width, int height) {
    this.imageUrl = requireNonBlank(imageUrl, "imageUrl");
    this.width = requirePositive(width, "width");
    this.height = requirePositive(height, "height");
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

  private static int requireSortOrder(int value) {
    int maxSortOrder = DisplayContentCategory.MAX_CONTENT_COUNT - 1;
    if (value < 0 || value > maxSortOrder) {
      throw new IllegalArgumentException("sortOrder must be between 0 and " + maxSortOrder + ".");
    }
    return value;
  }
}
