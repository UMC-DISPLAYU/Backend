package com.example.demo.domain.lounge.domain.entity;

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
@Table(name = "LoungeCommentImage")
public class LoungeCommentImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungeCommentImageId")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "loungeCommentId", nullable = false)
  private LoungeComment loungeComment;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int sortOrder;

  protected LoungeCommentImage() {}

  LoungeCommentImage(LoungeComment loungeComment, String imageUrl, int sortOrder) {
    this.loungeComment = Objects.requireNonNull(loungeComment, "loungeComment must not be null");
    this.imageUrl = requireValidImageUrl(imageUrl);
    this.sortOrder = requireNonNegative(sortOrder);
  }

  private static String requireValidImageUrl(String imageUrl) {
    if (imageUrl == null || imageUrl.isBlank() || imageUrl.length() > 2048) {
      throw new IllegalArgumentException("imageUrl must be valid");
    }
    return imageUrl;
  }

  private static int requireNonNegative(int sortOrder) {
    if (sortOrder < 0) {
      throw new IllegalArgumentException("sortOrder must not be negative");
    }
    return sortOrder;
  }
}
