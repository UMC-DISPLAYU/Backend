package com.example.demo.domain.lounge.domain.entity;

import com.example.demo.domain.lounge.domain.type.LoungeCommentStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@Entity
@Table(name = "LoungeComment")
public class LoungeComment extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungeCommentId")
  private Long id;

  @Column(nullable = false)
  private Long loungePostId;

  @Column(name = "parentCommentId")
  private Long parentCommentId;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId authorUserId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @OneToMany(mappedBy = "loungeComment", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @Getter(AccessLevel.NONE)
  private final List<LoungeCommentImage> images = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "commentStatus", nullable = false)
  private LoungeCommentStatus status;

  protected LoungeComment() {}

  public static LoungeComment createComment(
      Long loungePostId, UserId authorUserId, String content) {
    return createComment(loungePostId, authorUserId, content, List.of());
  }

  public static LoungeComment createComment(
      Long loungePostId, UserId authorUserId, String content, List<String> imageUrls) {
    LoungeComment comment =
        new LoungeComment(
            null, loungePostId, null, authorUserId, content, LoungeCommentStatus.ACTIVE);
    comment.addImages(imageUrls);
    return comment;
  }

  public static LoungeComment createReply(
      Long loungePostId, Long parentCommentId, UserId authorUserId, String content) {
    return createReply(loungePostId, parentCommentId, authorUserId, content, List.of());
  }

  public static LoungeComment createReply(
      Long loungePostId,
      Long parentCommentId,
      UserId authorUserId,
      String content,
      List<String> imageUrls) {
    LoungeComment reply =
        new LoungeComment(
            null, loungePostId, parentCommentId, authorUserId, content, LoungeCommentStatus.ACTIVE);
    reply.addImages(imageUrls);
    return reply;
  }

  public LoungeComment(
      Long id,
      Long loungePostId,
      Long parentCommentId,
      UserId authorUserId,
      String content,
      LoungeCommentStatus status) {
    this.id = id;
    this.loungePostId = requirePositive(loungePostId, "loungePostId");
    this.parentCommentId =
        parentCommentId == null ? null : requirePositive(parentCommentId, "parentCommentId");
    this.authorUserId = Objects.requireNonNull(authorUserId, "authorUserId must not be null.");
    this.content = requireNonBlank(content, "content");
    this.status = Objects.requireNonNull(status, "status must not be null.");
  }

  public boolean isReply() {
    return parentCommentId != null;
  }

  public List<String> getImageUrls() {
    return images.stream().map(LoungeCommentImage::getImageUrl).toList();
  }

  public boolean isRootComment() {
    return parentCommentId == null;
  }

  public boolean isActive() {
    return status == LoungeCommentStatus.ACTIVE;
  }

  @Override
  public void delete() {
    this.status = LoungeCommentStatus.DELETED;
    super.delete();
  }

  @Override
  public void restore() {
    this.status = LoungeCommentStatus.ACTIVE;
    super.restore();
  }

  private void addImages(List<String> imageUrls) {
    Objects.requireNonNull(imageUrls, "imageUrls must not be null");
    if (imageUrls.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("imageUrls must contain at most 5 images");
    }

    for (int index = 0; index < imageUrls.size(); index++) {
      images.add(new LoungeCommentImage(this, imageUrls.get(index), index));
    }
  }

  private static Long requirePositive(Long value, String fieldName) {
    if (value == null || value <= 0) {
      throw new IllegalArgumentException(fieldName + " must be positive.");
    }
    return value;
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank.");
    }
    return value;
  }
}
