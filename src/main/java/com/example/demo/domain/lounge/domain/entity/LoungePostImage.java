package com.example.demo.domain.lounge.domain.entity;

import com.example.demo.domain.lounge.domain.aggregate.LoungePost;
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
@Table(name = "LoungePostImage")
public class LoungePostImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungePostImageId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loungePostId", nullable = false)
  private LoungePost loungePost;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int sortOrder;

  protected LoungePostImage() {}

  public LoungePostImage(LoungePost loungePost, String imageUrl, int sortOrder) {
    this.loungePost = Objects.requireNonNull(loungePost, "loungePost must not be null");
    this.imageUrl = requireNonBlank(imageUrl, "imageUrl");
    this.sortOrder = requireNonNegative(sortOrder, "sortOrder");
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }

  private static int requireNonNegative(int value, String fieldName) {
    if (value < 0) {
      throw new IllegalArgumentException(fieldName + " must not be negative");
    }
    return value;
  }
}
