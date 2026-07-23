package com.example.demo.domain.displaycommunication.domain.aggregate;

import com.example.demo.domain.displaycommunication.domain.entity.DisplayReviewImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@Entity
@Table(name = "DisplayReview")
public class DisplayReview extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewId")
  private Long displayReviewId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private Long displayId;

  @Column(nullable = false)
  private Long userId;

  @OneToMany(mappedBy = "displayReview", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  private final List<DisplayReviewImage> images = new ArrayList<>();

  protected DisplayReview() {}

  private DisplayReview(Long displayId, Long userId, String content) {
    this.displayId = displayId;
    this.userId = userId;
    this.content = content;
  }

  public static DisplayReview create(
      Long displayId, Long userId, String content, List<ImageInfo> images) {
    DisplayReview review = new DisplayReview(displayId, userId, content);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      review.images.add(
          new DisplayReviewImage(review, image.imageUrl(), image.width(), image.height(), index));
    }
    return review;
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
