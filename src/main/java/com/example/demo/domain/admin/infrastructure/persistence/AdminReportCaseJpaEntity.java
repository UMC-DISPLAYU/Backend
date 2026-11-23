package com.example.demo.domain.admin.infrastructure.persistence;

import com.example.demo.domain.report.domain.type.ReportTargetType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

/**
 * 종결된 댓글 신고 묶음 한 건. reportCaseId는 그 회차 첫 신고 ID이고, 같은 댓글의 reportId가 lastReportId 이하인 신고는 이 묶음에서 종결된
 * 것으로 본다. 미종결 묶음은 저장하지 않고 접수 내역에서 계산한다.
 */
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "AdminReportCase",
    indexes =
        @Index(
            name = "IDX_ADMIN_REPORT_CASE_COMMENT",
            columnList = "commentType, commentId, lastReportId"))
public class AdminReportCaseJpaEntity implements Persistable<Long> {
  @Id private Long reportCaseId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
  private ReportTargetType commentType;

  @Column(nullable = false)
  private Long commentId;

  @Column(nullable = false)
  private Long lastReportId;

  @Column(nullable = false)
  private Long reportCount;

  @Column(nullable = false)
  private LocalDateTime lastReportedAt;

  @Column(nullable = false, length = 30)
  private String action;

  @Column(nullable = false, length = 1000)
  private String reason;

  @Column(nullable = false)
  private Long operatorId;

  @Column(nullable = false)
  private LocalDateTime processedAt;

  @Transient private boolean persisted;

  public AdminReportCaseJpaEntity(
      Long reportCaseId,
      ReportTargetType commentType,
      Long commentId,
      Long lastReportId,
      Long reportCount,
      LocalDateTime lastReportedAt,
      String action,
      String reason,
      Long operatorId,
      LocalDateTime processedAt) {
    this.reportCaseId = Objects.requireNonNull(reportCaseId, "reportCaseId must not be null.");
    this.commentType = Objects.requireNonNull(commentType, "commentType must not be null.");
    this.commentId = Objects.requireNonNull(commentId, "commentId must not be null.");
    this.lastReportId = Objects.requireNonNull(lastReportId, "lastReportId must not be null.");
    this.reportCount = Objects.requireNonNull(reportCount, "reportCount must not be null.");
    this.lastReportedAt =
        Objects.requireNonNull(lastReportedAt, "lastReportedAt must not be null.");
    this.action = Objects.requireNonNull(action, "action must not be null.");
    this.reason = Objects.requireNonNull(reason, "reason must not be null.");
    this.operatorId = Objects.requireNonNull(operatorId, "operatorId must not be null.");
    this.processedAt = Objects.requireNonNull(processedAt, "processedAt must not be null.");
  }

  /** 묶음 ID를 직접 지정하므로, 새 묶음은 병합 대신 항상 insert해 중복 종결을 키 충돌로 막는다. */
  @Override
  public Long getId() {
    return reportCaseId;
  }

  @Override
  public boolean isNew() {
    return !persisted;
  }

  @PostLoad
  void markPersisted() {
    persisted = true;
  }
}
