package com.example.demo.domain.display.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.display.application.result.DisplayScreeningPageResult;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchDisplayScreeningsServiceTest {

  private final DisplayScreeningQueryRepository repository =
      mock(DisplayScreeningQueryRepository.class);
  private final SearchDisplayScreeningsService service =
      new SearchDisplayScreeningsService(repository);

  @Test
  void searchUsesExtraRowToBuildNextCursor() {
    DisplayScreeningSearchQuery query =
        new DisplayScreeningSearchQuery(DisplayStatus.PENDING_REVIEW, null, 2);
    when(repository.search(query, 3)).thenReturn(List.of(summary(3L), summary(2L), summary(1L)));

    DisplayScreeningPageResult result = service.search(query);

    assertThat(result.items()).extracting(item -> item.displayId()).containsExactly(3L, 2L);
    assertThat(result.nextCursor()).isEqualTo(2L);
    assertThat(result.hasNext()).isTrue();
  }

  private static DisplayScreeningSummaryQueryResult summary(Long displayId) {
    return new DisplayScreeningSummaryQueryResult(
        displayId,
        "전시 " + displayId,
        1L,
        DisplayStatus.PENDING_REVIEW,
        LocalDateTime.of(2026, 9, 20, 1, 0),
        "학교",
        "대표자",
        LocalDate.of(2026, 10, 1),
        LocalDate.of(2026, 10, 7),
        "poster");
  }
}
