package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchDisplaysServiceTest {

  @Test
  void searchDisplaysUsesSizePlusOneAndReturnsNextCursor() {
    Clock clock = Clock.fixed(Instant.parse("2026-07-13T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    LocalDate today = LocalDate.now(clock);
    FakeSearchDisplayQueryRepository queryRepository =
        new FakeSearchDisplayQueryRepository(
            List.of(
                queryResult(1L, "디자인 졸업전시", today.plusDays(3)),
                queryResult(2L, "시각 디자인 전시", today.plusDays(5))));
    SearchDisplaysService service = new SearchDisplaysService(queryRepository, clock);

    SearchDisplayResult result =
        service.searchDisplays(new SearchDisplayQuery("디자인", null, null, null, null, 0L, 1));

    assertThat(queryRepository.requestedToday).isEqualTo(today);
    assertThat(queryRepository.requestedLimit).isEqualTo(2);
    assertThat(result.exhibitions()).hasSize(1);
    assertThat(result.pagination().nextCursor()).isEqualTo(1L);
    assertThat(result.pagination().size()).isEqualTo(1);
    assertThat(result.pagination().hasNext()).isTrue();
  }

  private static SearchDisplayQueryResult queryResult(
      Long displayId, String title, LocalDate endedAt) {
    return new SearchDisplayQueryResult(
        displayId,
        title,
        "https://cdn.displayu.com/posters/main.png",
        endedAt.minusDays(7),
        endedAt);
  }

  private static class FakeSearchDisplayQueryRepository implements SearchDisplayQueryRepository {

    private final List<SearchDisplayQueryResult> results;
    private LocalDate requestedToday;
    private int requestedLimit;

    private FakeSearchDisplayQueryRepository(List<SearchDisplayQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<SearchDisplayQueryResult> searchDisplays(
        SearchDisplayQuery query, LocalDate today, int limit) {
      requestedToday = today;
      requestedLimit = limit;
      return results;
    }
  }
}
