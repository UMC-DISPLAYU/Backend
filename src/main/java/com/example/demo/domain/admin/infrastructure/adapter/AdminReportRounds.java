package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.infrastructure.persistence.QAdminReportCaseJpaEntity;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.domain.report.infrastructure.persistence.QContentReportJpaEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 댓글 신고의 회차 계산. 같은 댓글에서 아직 종결 범위(lastReportId)에 들지 않은 신고가 진행 중인 회차이고, 그 첫 신고 ID가 묶음 ID다. 종결 뒤 들어온
 * 신고는 다음 회차가 된다.
 */
@Component
@RequiredArgsConstructor
class AdminReportRounds {
  static final Set<ReportTargetType> COMMENT_TYPES =
      Set.of(
          ReportTargetType.DISPLAY_REVIEW_REPLY,
          ReportTargetType.ARTWORK_FEELING_REPLY,
          ReportTargetType.PERSONAL_ARTWORK_FEELING_REPLY);
  static final QContentReportJpaEntity report = QContentReportJpaEntity.contentReportJpaEntity;
  private static final QAdminReportCaseJpaEntity resolved =
      QAdminReportCaseJpaEntity.adminReportCaseJpaEntity;

  private final JPAQueryFactory queryFactory;

  /** 종결된 묶음 범위에 들지 않은 댓글 신고. */
  static BooleanExpression pending() {
    return report
        .targetType
        .in(COMMENT_TYPES)
        .and(
            JPAExpressions.selectOne()
                .from(resolved)
                .where(
                    resolved.commentType.eq(report.targetType),
                    resolved.commentId.eq(report.targetId),
                    resolved.lastReportId.goe(report.reportId))
                .notExists());
  }

  /** reportCaseId가 진행 중인 회차의 첫 신고이면 그 회차의 신고를 ID 오름차순으로 반환한다. 아니면 비어 있다. */
  Optional<List<ContentReportJpaEntity>> pendingRound(Long reportCaseId) {
    ContentReportJpaEntity first =
        queryFactory
            .selectFrom(report)
            .where(report.reportId.eq(reportCaseId), pending())
            .fetchOne();
    if (first == null) return Optional.empty();
    List<ContentReportJpaEntity> round =
        queryFactory
            .selectFrom(report)
            .where(
                report.targetType.eq(first.getTargetType()),
                report.targetId.eq(first.getTargetId()),
                pending())
            .orderBy(report.reportId.asc())
            .fetch();
    return round.getFirst().getReportId().equals(reportCaseId)
        ? Optional.of(round)
        : Optional.empty();
  }

  /** 종결된 묶음에 들어간 신고를 ID 오름차순으로 반환한다. */
  List<ContentReportJpaEntity> resolvedRound(
      ReportTargetType commentType, Long commentId, Long reportCaseId, Long lastReportId) {
    return queryFactory
        .selectFrom(report)
        .where(
            report.targetType.eq(commentType),
            report.targetId.eq(commentId),
            report.reportId.between(reportCaseId, lastReportId))
        .orderBy(report.reportId.asc())
        .fetch();
  }
}
