package com.example.demo.domain.report.domain.model;

import com.example.demo.domain.report.domain.error.ReportErrorCode;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.Objects;

public record ContentReport(
    Long reportId,
    ReportTargetType targetType,
    Long targetId,
    Long reporterUserId,
    Long reportedUserId,
    ReportReason reason,
    String description,
    LocalDateTime createdAt) {
  public ContentReport {
    Objects.requireNonNull(targetType);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(createdAt);
    if (targetId == null
        || targetId <= 0
        || reporterUserId == null
        || reporterUserId <= 0
        || reportedUserId == null
        || reportedUserId <= 0
        || (reportId != null && reportId <= 0)) {
      throw new IllegalArgumentException("신고 관련 ID는 양수여야 합니다.");
    }
    if (reporterUserId.equals(reportedUserId)) {
      throw new BusinessException(ReportErrorCode.SELF_REPORT_NOT_ALLOWED);
    }
    if (description != null && description.length() > 1000) {
      throw new IllegalArgumentException("신고 상세 설명은 최대 1,000자여야 합니다.");
    }
  }

  public static ContentReport create(
      ReportTargetType targetType,
      Long targetId,
      Long reporterUserId,
      Long reportedUserId,
      ReportReason reason,
      LocalDateTime createdAt) {
    return create(targetType, targetId, reporterUserId, reportedUserId, reason, null, createdAt);
  }

  public static ContentReport create(
      ReportTargetType targetType,
      Long targetId,
      Long reporterUserId,
      Long reportedUserId,
      ReportReason reason,
      String description,
      LocalDateTime createdAt) {
    return new ContentReport(
        null, targetType, targetId, reporterUserId, reportedUserId, reason, description, createdAt);
  }
}
