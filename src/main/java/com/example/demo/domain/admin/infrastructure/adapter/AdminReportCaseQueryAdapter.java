package com.example.demo.domain.admin.infrastructure.adapter;

import static com.example.demo.domain.admin.infrastructure.adapter.AdminReportRounds.report;

import com.example.demo.domain.admin.application.port.CommentQueryPort;
import com.example.demo.domain.admin.application.port.ReportCaseQueryPort;
import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.result.CommentDetail;
import com.example.demo.domain.admin.application.result.ReportCaseDetail;
import com.example.demo.domain.admin.application.result.ReportCasePage;
import com.example.demo.domain.admin.application.result.ReportCaseSummary;
import com.example.demo.domain.admin.infrastructure.persistence.AdminReportCaseJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.QAdminReportCaseJpaEntity;
import com.example.demo.domain.admin.infrastructure.persistence.SpringDataAdminReportCaseJpaRepository;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminReportCaseQueryAdapter implements ReportCaseQueryPort {
  private static final String PENDING = "PENDING";
  private static final String RESOLVED = "RESOLVED";
  private static final QAdminReportCaseJpaEntity resolved =
      QAdminReportCaseJpaEntity.adminReportCaseJpaEntity;

  private final JPAQueryFactory queryFactory;
  private final AdminReportRounds rounds;
  private final SpringDataAdminReportCaseJpaRepository cases;
  private final CommentQueryPort comments;

  @Override
  public ReportCasePage search(ReportCaseSearchQuery query) {
    return RESOLVED.equals(query.status()) ? searchResolved(query) : searchPending(query);
  }

  @Override
  public ReportCaseDetail getDetail(Long reportCaseId) {
    AdminReportCaseJpaEntity closed = cases.findById(reportCaseId).orElse(null);
    if (closed != null) {
      List<ContentReportJpaEntity> round =
          rounds.resolvedRound(
              closed.getCommentType(),
              closed.getCommentId(),
              closed.getReportCaseId(),
              closed.getLastReportId());
      return detail(
          reportCaseId,
          RESOLVED,
          closed.getCommentType(),
          closed.getCommentId(),
          closed.getReportCount(),
          toInstant(closed.getLastReportedAt()),
          round,
          new ReportCaseDetail.Resolution(
              closed.getAction(),
              closed.getReason(),
              toInstant(closed.getProcessedAt()),
              closed.getOperatorId()));
    }
    List<ContentReportJpaEntity> round =
        rounds
            .pendingRound(reportCaseId)
            .orElseThrow(() -> new BusinessException(GlobalErrorCode.NOT_FOUND));
    ContentReportJpaEntity first = round.getFirst();
    return detail(
        reportCaseId,
        PENDING,
        first.getTargetType(),
        first.getTargetId(),
        round.size(),
        toInstant(round.getLast().getCreatedAt()),
        round,
        null);
  }

  private ReportCasePage searchPending(ReportCaseSearchQuery query) {
    NumberExpression<Long> caseId = report.reportId.min();
    NumberExpression<Long> reportCount = report.count();
    DateTimeExpression<LocalDateTime> lastReportedAt = report.createdAt.max();
    List<Tuple> rows =
        queryFactory
            .select(
                report.targetType, report.targetId, caseId, report.count(), report.createdAt.max())
            .from(report)
            .where(AdminReportRounds.pending())
            .groupBy(report.targetType, report.targetId)
            .having(query.cursor() == null ? null : caseId.lt(query.cursor()))
            .orderBy(caseId.desc())
            .limit(query.size() + 1L)
            .fetch();
    List<ReportCaseSummary> items =
        rows.stream()
            .limit(query.size())
            .map(
                row ->
                    new ReportCaseSummary(
                        row.get(caseId),
                        PENDING,
                        row.get(report.targetType).name(),
                        row.get(report.targetId),
                        row.get(reportCount),
                        toInstant(row.get(lastReportedAt))))
            .toList();
    return page(items, rows.size() > query.size());
  }

  private ReportCasePage searchResolved(ReportCaseSearchQuery query) {
    List<AdminReportCaseJpaEntity> rows =
        queryFactory
            .selectFrom(resolved)
            .where(query.cursor() == null ? null : resolved.reportCaseId.lt(query.cursor()))
            .orderBy(resolved.reportCaseId.desc())
            .limit(query.size() + 1L)
            .fetch();
    List<ReportCaseSummary> items =
        rows.stream()
            .limit(query.size())
            .map(
                row ->
                    new ReportCaseSummary(
                        row.getReportCaseId(),
                        RESOLVED,
                        row.getCommentType().name(),
                        row.getCommentId(),
                        row.getReportCount(),
                        toInstant(row.getLastReportedAt())))
            .toList();
    return page(items, rows.size() > query.size());
  }

  private ReportCasePage page(List<ReportCaseSummary> items, boolean hasNext) {
    return new ReportCasePage(items, hasNext ? items.getLast().reportCaseId() : null, hasNext);
  }

  private ReportCaseDetail detail(
      Long reportCaseId,
      String status,
      ReportTargetType commentType,
      Long commentId,
      long reportCount,
      Instant lastReportedAt,
      List<ContentReportJpaEntity> round,
      ReportCaseDetail.Resolution resolution) {
    CommentDetail current = currentComment(commentType, commentId);
    return new ReportCaseDetail(
        reportCaseId,
        status,
        commentType.name(),
        commentId,
        reportCount,
        lastReportedAt,
        current == null ? null : toComment(current.comment()),
        current == null ? null : toParent(current.parent()),
        current == null ? null : current.displayId(),
        current == null ? null : current.artworkId(),
        round.stream().map(this::toReport).toList(),
        resolution);
  }

  /** 원문이 보관되지 않아 댓글을 찾을 수 없으면 신고 내역만 보여 준다. */
  private CommentDetail currentComment(ReportTargetType commentType, Long commentId) {
    try {
      return comments.getCommentDetail(commentType.name(), commentId);
    } catch (BusinessException exception) {
      if (exception.errorCode() == GlobalErrorCode.NOT_FOUND) return null;
      throw exception;
    }
  }

  private ReportCaseDetail.Comment toComment(CommentDetail.Comment comment) {
    return comment == null
        ? null
        : new ReportCaseDetail.Comment(
            comment.authorUserId(),
            comment.authorNickname(),
            comment.content(),
            toAttachments(comment.attachments()),
            comment.createdAt(),
            comment.deletedAt());
  }

  private ReportCaseDetail.Parent toParent(CommentDetail.Parent parent) {
    return parent == null
        ? null
        : new ReportCaseDetail.Parent(
            parent.parentType(),
            parent.parentId(),
            parent.authorUserId(),
            parent.authorNickname(),
            parent.content(),
            toAttachments(parent.attachments()),
            parent.createdAt(),
            parent.deletedAt());
  }

  private List<ReportCaseDetail.Attachment> toAttachments(List<CommentDetail.Attachment> values) {
    return values == null
        ? List.of()
        : values.stream()
            .map(value -> new ReportCaseDetail.Attachment(value.url(), value.type()))
            .toList();
  }

  private ReportCaseDetail.Report toReport(ContentReportJpaEntity row) {
    return new ReportCaseDetail.Report(
        row.getReportId(),
        row.getReporterUserId(),
        row.getReason().name(),
        row.getDescription(),
        toInstant(row.getCreatedAt()));
  }

  private static Instant toInstant(LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }
}
