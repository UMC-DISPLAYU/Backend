package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "PersonalArtworkFeeling")
public class PersonalArtworkFeeling extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "personalFeelingId")
  private Long personalFeelingId;

  @Column(name = "personalArtworkId", nullable = false)
  private Long personalArtworkId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  protected PersonalArtworkFeeling() {}

  private PersonalArtworkFeeling(
      Long personalFeelingId, Long personalArtworkId, Long userId, String content) {
    this.personalFeelingId = personalFeelingId;
    this.personalArtworkId = personalArtworkId;
    this.userId = userId;
    this.content = content;
  }

  public static PersonalArtworkFeeling create(Long personalArtworkId, Long userId, String content) {
    return new PersonalArtworkFeeling(null, personalArtworkId, userId, content);
  }

  public boolean belongsToArtwork(Long personalArtworkId) {
    return this.personalArtworkId.equals(personalArtworkId);
  }
}
