package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public final class CommentResponses {
  private CommentResponses() {}

  public record Detail(Comment comment, Parent parent, Long displayId, Long artworkId) {}

  public record Attachment(String url, String type) {}

  public record Comment(
      String commentType,
      Long commentId,
      Long authorUserId,
      String authorNickname,
      String content,
      List<Attachment> attachments,
      Instant createdAt,
      Instant deletedAt) {}

  public record Parent(
      String parentType,
      Long parentId,
      Long authorUserId,
      String authorNickname,
      String content,
      List<Attachment> attachments,
      Instant createdAt,
      Instant deletedAt) {}
}
