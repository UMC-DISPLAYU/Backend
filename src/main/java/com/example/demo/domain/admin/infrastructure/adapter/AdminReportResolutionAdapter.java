package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.CommentDeletionPort;
import com.example.demo.domain.admin.application.port.ReportResolutionPort;
import com.example.demo.domain.admin.application.result.ReportResolutionResult;
import com.example.demo.domain.admin.infrastructure.persistence.AdminReportCaseJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.SpringDataAdminReportCaseJpaRepository;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminReportResolutionAdapter implements ReportResolutionPort {
  private static final String DELETE_COMMENT = "DELETE_COMMENT";

  private final AdminReportRounds rounds;
  private final SpringDataAdminReportCaseJpaRepository cases;
  private final CommentDeletionPort comments;
  private final Clock clock;

  /** 처리 시점까지 접수된 회차 신고만 종결하고, 댓글 삭제와 종결 저장을 한 트랜잭션으로 처리한다. */
  @Override
  @Transactional
  public ReportResolutionResult resolve(
      Long reportCaseId, String action, Long operatorId, String reason) {
    if (cases.existsById(reportCaseId)) {
      throw new BusinessException(GlobalErrorCode.CONFLICT);
    }
    List<ContentReportJpaEntity> round =
        rounds
            .pendingRound(reportCaseId)
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    ContentReportJpaEntity first = round.getFirst();
    ContentReportJpaEntity last = round.getLast();
    Instant processedAt =
        DELETE_COMMENT.equals(action) ? deleteComment(first, operatorId, reason) : clock.instant();
    try {
      cases.saveAndFlush(
          new AdminReportCaseJpaEntity(
              reportCaseId,
              first.getTargetType(),
              first.getTargetId(),
              last.getReportId(),
              (long) round.size(),
              last.getCreatedAt(),
              action,
              reason,
              operatorId,
              LocalDateTime.ofInstant(processedAt, ZoneOffset.UTC)));
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(GlobalErrorCode.CONFLICT);
    }
    return new ReportResolutionResult(
        reportCaseId,
        "RESOLVED",
        action,
        first.getTargetType().name(),
        first.getTargetId(),
        operatorId,
        processedAt);
  }

  /** 기존 댓글 삭제를 재사용한다. 이미 삭제된 댓글은 처리할 수 없는 상태라 CONFLICT다. */
  private Instant deleteComment(ContentReportJpaEntity report, Long operatorId, String reason) {
    try {
      return comments
          .delete(report.getTargetType().name(), report.getTargetId(), operatorId, reason)
          .processedAt();
    } catch (BusinessException exception) {
      if (exception.errorCode() == GlobalErrorCode.NOT_FOUND) {
        throw new BusinessException(GlobalErrorCode.CONFLICT);
      }
      throw exception;
    }
  }
}
