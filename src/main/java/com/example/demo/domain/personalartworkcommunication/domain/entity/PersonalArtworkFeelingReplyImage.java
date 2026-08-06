package com.example.demo.domain.personalartworkcommunication.domain.entity;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
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
@Table(name = "PersonalArtworkFeelingReplyImage")
public class PersonalArtworkFeelingReplyImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingReplyImageId")
  private Long personalFeelingReplyImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalFeelingReplyId", nullable = false)
  private PersonalArtworkFeelingReply personalArtworkFeelingReply;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected PersonalArtworkFeelingReplyImage() {}

  public PersonalArtworkFeelingReplyImage(
      PersonalArtworkFeelingReply personalArtworkFeelingReply,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.personalArtworkFeelingReply = personalArtworkFeelingReply;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
