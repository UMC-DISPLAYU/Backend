package com.example.demo.domain.artworkcommunication.domain.entity;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
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
@Table(name = "ArtworkQuestionReplyImage")
public class ArtworkQuestionReplyImage extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "questionReplyImageId")
  private Long questionReplyImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionReplyId", nullable = false)
  private ArtworkQuestionReply artworkQuestionReply;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected ArtworkQuestionReplyImage() {}

  public ArtworkQuestionReplyImage(
      ArtworkQuestionReply artworkQuestionReply,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.artworkQuestionReply = artworkQuestionReply;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
