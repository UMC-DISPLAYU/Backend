package com.example.demo.domain.admin.presentation.response;

import java.time.Instant;
import java.util.List;

public final class ReportCaseResponses {
  private ReportCaseResponses() {}

  public record Page(List<Summary> items, Long nextCursor, boolean hasNext) {}

  public record Summary(
      Long reportCaseId,
      String status,
      String commentType,
      Long commentId,
      long reportCount,
      Instant lastReportedAt) {}

  public record Detail(
      Long reportCaseId,
      String status,
      String commentType,
      Long commentId,
      long reportCount,
      Instant lastReportedAt,
      Comment comment,
      Parent parent,
      Long displayId,
      Long artworkId,
      List<Report> reports,
      Resolution resolution) {}

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

  public record Report(
      Long reportId,
      Long reporterUserId,
      String reasonCode,
      String description,
      Instant reportedAt) {}

  public record Resolution(String action, String reason, Instant processedAt, Long operatorId) {}
}
