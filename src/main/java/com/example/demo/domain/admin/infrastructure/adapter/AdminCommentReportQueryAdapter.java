package com.example.demo.domain.admin.infrastructure.adapter;

import com.example.demo.domain.admin.application.port.CommentReportQueryPort;
import com.example.demo.domain.admin.application.query.CommentReportSearchQuery;
import com.example.demo.domain.admin.application.result.CommentReportPage;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.domain.report.infrastructure.persistence.SpringDataContentReportJpaRepository;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentReportQueryAdapter implements CommentReportQueryPort {
  private final SpringDataContentReportJpaRepository reports;

  @Override
  public CommentReportPage search(
      String commentType, Long commentId, CommentReportSearchQuery query) {
    ReportTargetType targetType = ReportTargetType.valueOf(commentType);
    var page = PageRequest.of(0, query.size() + 1);
    List<ContentReportJpaEntity> rows =
        query.cursor() == null
            ? reports.findByTargetTypeAndTargetIdOrderByReportIdDesc(targetType, commentId, page)
            : reports.findByTargetTypeAndTargetIdAndReportIdLessThanOrderByReportIdDesc(
                targetType, commentId, query.cursor(), page);
    boolean hasNext = rows.size() > query.size();
    List<CommentReportPage.Item> items =
        rows.stream().limit(query.size()).map(this::toItem).toList();
    Long nextCursor = hasNext ? items.getLast().reportId() : null;
    return new CommentReportPage(items, nextCursor, hasNext);
  }

  private CommentReportPage.Item toItem(ContentReportJpaEntity row) {
    return new CommentReportPage.Item(
        row.getReportId(),
        row.getReporterUserId(),
        row.getReason().name(),
        row.getDescription(),
        row.getCreatedAt().toInstant(ZoneOffset.UTC));
  }
}
