package com.example.demo.domain.admin.infrastructure.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.result.OperationLog;
import com.example.demo.domain.admin.application.result.OperationLogPage;
import com.example.demo.global.config.QuerydslConfig;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({AdminOperationLogAdapter.class, QuerydslConfig.class})
class AdminOperationLogAdapterTest {
  private static final Instant BASE = Instant.parse("2026-11-20T00:00:00Z");
  @Autowired private AdminOperationLogAdapter adapter;

  @Test
  void recordsAndReturnsStoredOperation() {
    OperationLog saved =
        adapter.record(OperationLog.of("DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, "운영 검토", BASE));

    assertThat(saved.operationId()).isPositive();
    assertThat(page(query(null, null, null, null, null, null, null, null, 20)).items())
        .containsExactly(saved);
    assertThat(saved.processedAt()).isEqualTo(BASE);
  }

  @Test
  void rejectsOperationWithoutProcessedAt() {
    assertThatThrownBy(
            () ->
                adapter.record(OperationLog.of("DISPLAY_REVIEW", 31L, "DELETE", 7L, "운영 검토", null)))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  void filtersByEveryConditionTogetherAndSeparately() {
    OperationLog target = save("DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, "욕설 신고 확인", 0);
    OperationLog otherTargetId = save("DISPLAY_REVIEW_REPLY", 42L, "DELETE", 7L, "욕설 신고 확인", 0);
    OperationLog otherType = save("ARTWORK_FEELING_REPLY", 41L, "DELETE", 7L, "욕설 신고 확인", 0);
    OperationLog otherOperator = save("DISPLAY_REVIEW_REPLY", 41L, "DELETE", 8L, "욕설 신고 확인", 0);
    OperationLog otherAction = save("DISPLAY_REVIEW_REPLY", 41L, "SUSPEND", 7L, "욕설 신고 확인", 0);
    OperationLog otherReason = save("DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, "광고 글", 0);

    assertThat(ids(query("욕설", null, null, "DISPLAY_REVIEW_REPLY", 41L, "DELETE", 7L, null, 20)))
        .containsExactly(target.operationId());
    assertThat(ids(query(null, null, null, "DISPLAY_REVIEW_REPLY", 41L, null, null, null, 20)))
        .containsExactly(
            otherReason.operationId(),
            otherAction.operationId(),
            otherOperator.operationId(),
            target.operationId());
    assertThat(ids(query(null, null, null, "ARTWORK_FEELING_REPLY", null, null, null, null, 20)))
        .containsExactly(otherType.operationId());
    assertThat(ids(query(null, null, null, null, null, "SUSPEND", null, null, 20)))
        .containsExactly(otherAction.operationId());
    assertThat(ids(query(null, null, null, null, null, null, 8L, null, 20)))
        .containsExactly(otherOperator.operationId());
    assertThat(ids(query("광고", null, null, null, null, null, null, null, 20)))
        .containsExactly(otherReason.operationId());
    assertThat(ids(query(null, null, null, "DISPLAY_REVIEW_REPLY", 42L, null, null, null, 20)))
        .containsExactly(otherTargetId.operationId());
  }

  @Test
  void searchesReasonIgnoringCaseAndTreatsWildcardsAsText() {
    OperationLog matching = save("DISPLAY_REVIEW", 31L, "DELETE", 7L, "Spam 100%_확인", 0);
    save("DISPLAY_REVIEW", 32L, "DELETE", 7L, "Spam 1000 확인", 0);

    assertThat(ids(query("spam", null, null, null, null, null, null, null, 20))).hasSize(2);
    assertThat(ids(query("%_", null, null, null, null, null, null, null, 20)))
        .containsExactly(matching.operationId());
    assertThat(ids(query("없는 사유", null, null, null, null, null, null, null, 20))).isEmpty();
  }

  @Test
  void includesFromAndExcludesTo() {
    OperationLog before = save("DISPLAY_REVIEW", 31L, "DELETE", 7L, "사유", -1);
    OperationLog atFrom = save("DISPLAY_REVIEW", 32L, "DELETE", 7L, "사유", 0);
    OperationLog inside = save("DISPLAY_REVIEW", 33L, "DELETE", 7L, "사유", 59);
    OperationLog atTo = save("DISPLAY_REVIEW", 34L, "DELETE", 7L, "사유", 60);

    assertThat(ids(query(null, BASE, BASE.plusSeconds(60 * 60), null, null, null, null, null, 20)))
        .containsExactly(inside.operationId(), atFrom.operationId());
    assertThat(ids(query(null, null, BASE, null, null, null, null, null, 20)))
        .containsExactly(before.operationId());
    assertThat(ids(query(null, BASE.plusSeconds(60 * 60), null, null, null, null, null, null, 20)))
        .containsExactly(atTo.operationId());
  }

  @Test
  void pagesByOperationIdDescendingWithCursor() {
    OperationLog first = save("DISPLAY_REVIEW", 31L, "DELETE", 7L, "사유", 0);
    OperationLog second = save("DISPLAY_REVIEW", 32L, "DELETE", 7L, "사유", 0);
    OperationLog third = save("DISPLAY_REVIEW", 33L, "DELETE", 7L, "사유", 0);

    OperationLogPage firstPage = page(query(null, null, null, null, null, null, null, null, 2));
    assertThat(firstPage.items())
        .extracting(OperationLog::operationId)
        .containsExactly(third.operationId(), second.operationId());
    assertThat(firstPage.hasNext()).isTrue();
    assertThat(firstPage.nextCursor()).isEqualTo(second.operationId());

    OperationLogPage lastPage =
        page(query(null, null, null, null, null, null, null, firstPage.nextCursor(), 2));
    assertThat(lastPage.items())
        .extracting(OperationLog::operationId)
        .containsExactly(first.operationId());
    assertThat(lastPage.hasNext()).isFalse();
    assertThat(lastPage.nextCursor()).isNull();

    OperationLogPage exact = page(query(null, null, null, null, null, null, null, null, 3));
    assertThat(exact.items()).hasSize(3);
    assertThat(exact.hasNext()).isFalse();
    assertThat(exact.nextCursor()).isNull();
  }

  private OperationLog save(
      String type, Long id, String action, Long operatorId, String reason, int minutes) {
    return adapter.record(
        OperationLog.of(type, id, action, operatorId, reason, BASE.plusSeconds(minutes * 60L)));
  }

  private OperationLogSearchQuery query(
      String q,
      Instant from,
      Instant to,
      String targetType,
      Long targetId,
      String action,
      Long operatorId,
      Long cursor,
      int size) {
    return new OperationLogSearchQuery(
        q, from, to, targetType, targetId, action, operatorId, cursor, size);
  }

  private OperationLogPage page(OperationLogSearchQuery query) {
    return adapter.search(query);
  }

  private java.util.List<Long> ids(OperationLogSearchQuery query) {
    return page(query).items().stream().map(OperationLog::operationId).toList();
  }
}
