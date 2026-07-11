package com.example.demo.domain.lounge.domain.aggregate;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.domain.type.LoungePostStatus;
import com.example.demo.domain.lounge.domain.vo.UserId;
import com.example.demo.global.entity.SoftDeleteBaseEntity;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;

@Getter
@Entity
@Table(name = "LoungePost")
public class LoungePost extends SoftDeleteBaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loungePostId")
  private Long id;

  @Embedded
  @AttributeOverride(name = "value", column = @Column(name = "userId", nullable = false))
  private UserId authorUserId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String postImageUrl;

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
    return create(authorUserId, title, null, content, category);
  }

  public static LoungePost create(
      UserId authorUserId,
      String title,
      String postImageUrl,
      String content,
      LoungePostCategory category) {
    return new LoungePost(
        null, authorUserId, title, postImageUrl, content, category, LoungePostStatus.ACTIVE);
  }

  public LoungePost(
      Long id,
      UserId authorUserId,
      String title,
      String content,
      LoungePostCategory category,
      LoungePostStatus status) {
    this(id, authorUserId, title, null, content, category, status);
  }

  public LoungePost(
      Long id,
      UserId authorUserId,
      String title,
      String postImageUrl,
      String content,
      LoungePostCategory category,
      LoungePostStatus status) {
    this.id = id;
    this.authorUserId = Objects.requireNonNull(authorUserId, "authorUserId must not be null");
    changeContent(title, postImageUrl, content);
    changeCategory(category);
    this.status = Objects.requireNonNullElse(status, LoungePostStatus.ACTIVE);
  }

  public void changeContent(String title, String content) {
    changeContent(title, this.postImageUrl, content);
  }

  public void changeContent(String title, String postImageUrl, String content) {
    this.title = requireNonBlack(title, "title");
    this.postImageUrl = postImageUrl;
    this.content = requireNonBlack(content, "content");
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

  private static String requireNonBlack(String value, String fieldName) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }
}
