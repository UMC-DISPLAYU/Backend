package com.example.demo.domain.personalartworkcommunication.domain.entity;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
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
@Table(name = "PersonalArtworkQuestionReplyImage")
public class PersonalArtworkQuestionReplyImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionReplyImageId")
  private Long personalQuestionReplyImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalQuestionReplyId", nullable = false)
  private PersonalArtworkQuestionReply personalArtworkQuestionReply;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected PersonalArtworkQuestionReplyImage() {}

  public PersonalArtworkQuestionReplyImage(
      PersonalArtworkQuestionReply personalArtworkQuestionReply,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.personalArtworkQuestionReply = personalArtworkQuestionReply;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
