package com.example.demo.domain.displaycommunication.domain.entity;

import com.example.demo.domain.displaycommunication.domain.aggregate.DisplayReviewReply;
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
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReviewReplyImage")
public class DisplayReviewReplyImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewReplyImageId")
  private Long displayReviewReplyImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "displayReviewReplyId", nullable = false)
  private DisplayReviewReply displayReviewReply;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected DisplayReviewReplyImage() {}

  public DisplayReviewReplyImage(
      DisplayReviewReply displayReviewReply,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.displayReviewReply = displayReviewReply;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
