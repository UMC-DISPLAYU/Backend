package com.example.demo.domain.personalartworkcommunication.domain.aggregate;

import com.example.demo.domain.personalartworkcommunication.domain.entity.PersonalArtworkFeelingImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

  @OneToMany(mappedBy = "personalArtworkFeeling", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private final List<PersonalArtworkFeelingImage> images = new ArrayList<>();

  protected PersonalArtworkFeeling() {}

  private PersonalArtworkFeeling(
      Long personalFeelingId, Long personalArtworkId, Long userId, String content) {
    this.personalFeelingId = personalFeelingId;
    this.personalArtworkId = personalArtworkId;
    this.userId = userId;
    this.content = content;
  }

  public static PersonalArtworkFeeling create(
      Long personalArtworkId, Long userId, String content, List<ImageInfo> images) {
    PersonalArtworkFeeling feeling =
        new PersonalArtworkFeeling(null, personalArtworkId, userId, content);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      feeling.images.add(
          new PersonalArtworkFeelingImage(
              feeling, image.imageUrl(), image.width(), image.height(), index));
    }
    return feeling;
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public boolean belongsToArtwork(Long personalArtworkId) {
    return this.personalArtworkId.equals(personalArtworkId);
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
