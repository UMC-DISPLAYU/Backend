package com.example.demo.domain.displaycommunication.domain.entity;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReview;
import com.example.demo.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReviewImage")
public class DisplayReviewImage extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reviewImageId")
  private Long reviewImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayReviewId", nullable = false)
  private DisplayReview displayReview;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected DisplayReviewImage() {}

  public DisplayReviewImage(
      DisplayReview displayReview, String imageUrl, int width, int height, int sortOrder) {
    this.displayReview = displayReview;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
