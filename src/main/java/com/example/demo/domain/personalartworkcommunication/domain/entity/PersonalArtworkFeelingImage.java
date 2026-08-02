package com.example.demo.domain.personalartworkcommunication.domain.entity;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
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
@Table(name = "PersonalArtworkFeelingImage")
public class PersonalArtworkFeelingImage extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingImageId")
  private Long personalFeelingImageId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "personalFeelingId", nullable = false)
  private PersonalArtworkFeeling personalArtworkFeeling;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String imageUrl;

  @Column(nullable = false)
  private int width;

  @Column(nullable = false)
  private int height;

  @Column(nullable = false)
  private int sortOrder;

  protected PersonalArtworkFeelingImage() {}

  public PersonalArtworkFeelingImage(
      PersonalArtworkFeeling personalArtworkFeeling,
      String imageUrl,
      int width,
      int height,
      int sortOrder) {
    this.personalArtworkFeeling = personalArtworkFeeling;
    this.imageUrl = imageUrl;
    this.width = width;
    this.height = height;
    this.sortOrder = sortOrder;
  }
}
