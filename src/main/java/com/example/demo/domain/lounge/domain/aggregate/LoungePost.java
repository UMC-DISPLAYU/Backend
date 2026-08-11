package com.example.demo.domain.lounge.domain.aggregate;

import com.example.demo.domain.lounge.domain.entity.LoungePostImage;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@Table(name = "LoungePost")
public class LoungePost extends SoftDeleteBaseEntity {
  private static final int MAX_IMAGE_COUNT = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungePostId")
  private Long id;

  @Version private Long version;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId authorUserId;

  @Column(nullable = false)
  private String title;

  @OneToMany(mappedBy = "loungePost", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("sortOrder ASC")
  @BatchSize(size = 50)
  @Getter(AccessLevel.NONE)
  private final List<LoungePostImage> images = new ArrayList<>();

  @Column(nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "postStatus", nullable = false)
  private LoungePostStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoungePostCategory category;

  protected LoungePost() {}

  public static LoungePost create(
      UserId authorUserId, String title, String content, LoungePostCategory category) {
    return create(authorUserId, title, List.of(), content, category);
  }

  public static LoungePost create(
      UserId authorUserId,
      String title,
      List<String> postImageUrls,
      String content,
      LoungePostCategory category) {
    return new LoungePost(
        null, authorUserId, title, postImageUrls, content, category, LoungePostStatus.ACTIVE);
  }

  public LoungePost(
      Long id,
      UserId authorUserId,
      String title,
      String content,
      LoungePostCategory category,
      LoungePostStatus status) {
    this(id, authorUserId, title, List.of(), content, category, status);
  }

  public LoungePost(
      Long id,
      UserId authorUserId,
      String title,
      List<String> postImageUrls,
      String content,
      LoungePostCategory category,
      LoungePostStatus status) {
    this.id = id;
    this.authorUserId = Objects.requireNonNull(authorUserId, "authorUserId must not be null");
    changeContent(title, content);
    replaceImages(postImageUrls);
    changeCategory(category);
    this.status = Objects.requireNonNullElse(status, LoungePostStatus.ACTIVE);
  }

  public List<String> getPostImageUrls() {
    return images.stream().map(LoungePostImage::getImageUrl).toList();
  }

  public void changeContent(String title, String content) {
    this.title = requireNonBlank(title, "title");
    this.content = requireNonBlank(content, "content");
  }

  public void replaceImages(List<String> postImageUrls) {
    Objects.requireNonNull(postImageUrls, "postImageUrls must not be null");
    if (postImageUrls.size() > MAX_IMAGE_COUNT) {
      throw new IllegalArgumentException("postImageUrls must contain at most 5 images");
    }
    images.clear();
    for (int index = 0; index < postImageUrls.size(); index++) {
      addImage(postImageUrls.get(index), index);
    }
  }

  private void addImage(String imageUrl, int sortOrder) {
    images.add(new LoungePostImage(this, imageUrl, sortOrder));
  }

  public void changeCategory(LoungePostCategory category) {
    this.category = Objects.requireNonNull(category, "category must not be null");
  }

  public void hide() {
    this.status = LoungePostStatus.HIDDEN;
  }

  public void activate() {
    this.status = LoungePostStatus.ACTIVE;
  }

  public boolean isActive() {
    return this.status == LoungePostStatus.ACTIVE;
  }

  public boolean isAuthoredBy(Long userId) {
    return userId != null && this.authorUserId.value().equals(userId);
  }

  @Override
  public void delete() {
    this.status = LoungePostStatus.DELETED;
    super.delete();
  }

  @Override
  public void restore() {
    this.status = LoungePostStatus.ACTIVE;
    super.restore();
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }
}
