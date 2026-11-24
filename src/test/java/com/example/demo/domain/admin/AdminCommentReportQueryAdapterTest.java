package com.example.demo.domain.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.admin.application.query.CommentReportSearchQuery;
import com.example.demo.domain.admin.infrastructure.adapter.AdminCommentReportQueryAdapter;
import com.example.demo.domain.report.domain.type.ReportReason;
import com.example.demo.domain.report.domain.type.ReportTargetType;
import com.example.demo.domain.report.infrastructure.persistence.ContentReportJpaEntity;
import com.example.demo.domain.report.infrastructure.persistence.SpringDataContentReportJpaRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class AdminCommentReportQueryAdapterTest {
  private static final LocalDateTime REPORTED_AT =
      LocalDateTime.ofInstant(Instant.parse("2026-11-18T01:02:03Z"), ZoneOffset.UTC);
  private final SpringDataContentReportJpaRepository repository =
      mock(SpringDataContentReportJpaRepository.class);
  private final AdminCommentReportQueryAdapter adapter =
      new AdminCommentReportQueryAdapter(repository);

  @Test
  void fetchesOneExtraRowAndBuildsStableNextCursor() {
    var query = new CommentReportSearchQuery(null, 2);
    when(repository.findByTargetTypeAndTargetIdOrderByReportIdDesc(
            ReportTargetType.DISPLAY_REVIEW_REPLY, 41L, Pageable.ofSize(3)))
        .thenReturn(List.of(row(9L), row(8L), row(7L)));

    var result = adapter.search("DISPLAY_REVIEW_REPLY", 41L, query);

    assertThat(result.items()).hasSize(2);
    assertThat(result.items().getFirst().reportId()).isEqualTo(9L);
    assertThat(result.items().getFirst().reporterUserId()).isEqualTo(109L);
    assertThat(result.items().getFirst().reasonCode()).isEqualTo("OTHER");
    assertThat(result.items().getFirst().description()).isEqualTo("설명 9");
    assertThat(result.items().getFirst().reportedAt())
        .isEqualTo(REPORTED_AT.toInstant(ZoneOffset.UTC));
    assertThat(result.nextCursor()).isEqualTo(8L);
    assertThat(result.hasNext()).isTrue();
  }

  @Test
  void appliesExclusiveCursorAndReturnsNullCursorAtEnd() {
    var query = new CommentReportSearchQuery(8L, 2);
    when(repository.findByTargetTypeAndTargetIdAndReportIdLessThanOrderByReportIdDesc(
            ReportTargetType.ARTWORK_FEELING_REPLY, 41L, 8L, Pageable.ofSize(3)))
        .thenReturn(List.of(row(7L)));

    var result = adapter.search("ARTWORK_FEELING_REPLY", 41L, query);

    assertThat(result.items()).extracting(item -> item.reportId()).containsExactly(7L);
    assertThat(result.nextCursor()).isNull();
    assertThat(result.hasNext()).isFalse();
    verify(repository)
        .findByTargetTypeAndTargetIdAndReportIdLessThanOrderByReportIdDesc(
            ReportTargetType.ARTWORK_FEELING_REPLY, 41L, 8L, Pageable.ofSize(3));
  }

  private ContentReportJpaEntity row(Long reportId) {
    return new ContentReportJpaEntity(
        reportId,
        ReportTargetType.DISPLAY_REVIEW_REPLY,
        41L,
        100L + reportId,
        200L,
        ReportReason.OTHER,
        "설명 " + reportId,
        REPORTED_AT);
  }
}
