package com.example.demo.domain.personalartworkcommunication.domain.entity;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
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
@Table(name = "PersonalArtworkQuestionImage")
public class PersonalArtworkQuestionImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalQuestionImageId")
  private Long personalQuestionImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalQuestionId", nullable = false)
  private PersonalArtworkQuestion personalArtworkQuestion;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected PersonalArtworkQuestionImage() {}

  public PersonalArtworkQuestionImage(
      PersonalArtworkQuestion personalArtworkQuestion,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.personalArtworkQuestion = personalArtworkQuestion;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
