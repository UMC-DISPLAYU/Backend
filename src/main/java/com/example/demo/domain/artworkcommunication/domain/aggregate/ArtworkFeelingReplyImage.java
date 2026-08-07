package com.example.demo.domain.artworkcommunication.domain.aggregate;

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
@Table(name = "ArtworkFeelingReplyImage")
public class ArtworkFeelingReplyImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "feelingReplyImageId")
  private Long feelingReplyImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "feelingReplyId", nullable = false)
  private ArtworkFeelingReply artworkFeelingReply;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected ArtworkFeelingReplyImage() {}

  ArtworkFeelingReplyImage(
      ArtworkFeelingReply artworkFeelingReply,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.artworkFeelingReply = artworkFeelingReply;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
