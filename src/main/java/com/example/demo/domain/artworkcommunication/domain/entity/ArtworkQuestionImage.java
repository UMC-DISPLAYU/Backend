package com.example.demo.domain.artworkcommunication.domain.entity;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
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
@Table(name = "ArtworkQuestionImage")
public class ArtworkQuestionImage extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "questionImageId")
  private Long questionImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "questionId", nullable = false)
  private ArtworkQuestion artworkQuestion;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected ArtworkQuestionImage() {}

  public ArtworkQuestionImage(
      ArtworkQuestion artworkQuestion, String imageUrl, int width, int height, int sortOrder) {
    this.artworkQuestion = artworkQuestion;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
