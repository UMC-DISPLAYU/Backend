package com.example.demo.domain.displaycommunication.domain.aggregate;

import com.example.demo.domain.displaycommunication.domain.entity.DisplayReviewReplyImage;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "DisplayReviewReply")
public class DisplayReviewReply extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "displayReviewReplyId")
  private Long displayReviewReplyId;

  @Column(name = "content", nullable = false, length = 300)
  private String content;

  @Column(name = "displayReviewId", nullable = false)
  private Long displayReviewId;

  @Column(name = "userId", nullable = false)
  private Long userId;

  @OneToMany(mappedBy = "displayReviewReply", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  @Getter(AccessLevel.NONE)
  private final List<DisplayReviewReplyImage> images = new ArrayList<>();

  protected DisplayReviewReply() {}

  private DisplayReviewReply(Long displayReviewId, Long userId, String content) {
    this.displayReviewId = displayReviewId;
    this.userId = userId;
    this.content = content;
  }

  public static DisplayReviewReply create(
      Long displayReviewId, Long userId, String content, List<ImageInfo> images) {
    DisplayReviewReply reply = new DisplayReviewReply(displayReviewId, userId, content);
    for (int index = 0; index < images.size(); index++) {
      ImageInfo image = images.get(index);
      reply.images.add(
          new DisplayReviewReplyImage(
              reply, image.imageUrl(), image.width(), image.height(), index));
    }
    return reply;
  }

  public boolean belongsToReview(Long displayReviewId) {
    return this.displayReviewId.equals(displayReviewId);
  }

  public boolean isWrittenBy(Long userId) {
    return this.userId.equals(userId);
  }

  public List<DisplayReviewReplyImage> getImages() {
    return Collections.unmodifiableList(images);
  }

  public record ImageInfo(String imageUrl, int width, int height) {}
}
