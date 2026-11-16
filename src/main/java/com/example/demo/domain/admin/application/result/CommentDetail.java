package com.example.demo.domain.admin.application.result;

import java.time.Instant;
import java.util.List;

public record CommentDetail(Comment comment, Parent parent, Long displayId, Long artworkId) {
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
