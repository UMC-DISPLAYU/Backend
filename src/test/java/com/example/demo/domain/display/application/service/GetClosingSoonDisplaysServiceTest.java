package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetClosingSoonDisplaysServiceTest {

  @Test
  void getClosingSoonDisplaysUsesSizePlusOneAndReturnsNextCursor() {
    Clock clock = Clock.fixed(Instant.parse("2026-07-13T00:00:00Z"), ZoneId.of("Asia/Seoul"));
    LocalDate today = LocalDate.now(clock);
    FakeClosingSoonDisplayQueryRepository queryRepository =
        new FakeClosingSoonDisplayQueryRepository(
            List.of(
                queryResult(1L, "오늘 종료 전시", today),
                queryResult(2L, "3일 남은 전시", today.plusDays(3))));
    GetClosingSoonDisplaysService service =
        new GetClosingSoonDisplaysService(queryRepository, clock);

    ClosingSoonDisplayResult result =
        service.getClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1));

    assertThat(queryRepository.requestedToday).isEqualTo(today);
    assertThat(queryRepository.requestedLimit).isEqualTo(2);
    assertThat(result.exhibitions())
        .extracting(ClosingSoonDisplayResult.ExhibitionResult::dayLeft)
        .containsExactly(0L);
    assertThat(result.pagination().nextCursor()).isEqualTo(today + ":1");
    assertThat(result.pagination().size()).isEqualTo(1);
    assertThat(result.pagination().hasNext()).isTrue();
  }

  private static ClosingSoonDisplayQueryResult queryResult(
      Long displayId, String title, LocalDate endedAt) {
    return new ClosingSoonDisplayQueryResult(
        displayId,
        title,
        "https://cdn.displayu.com/posters/main.png",
        endedAt.minusDays(7),
        endedAt);
  }

  private static class FakeClosingSoonDisplayQueryRepository
      implements ClosingSoonDisplayQueryRepository {

    private final List<ClosingSoonDisplayQueryResult> results;
    private LocalDate requestedToday;
    private int requestedLimit;

    private FakeClosingSoonDisplayQueryRepository(List<ClosingSoonDisplayQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<ClosingSoonDisplayQueryResult> findClosingSoonDisplays(
        ClosingSoonDisplayQuery query, LocalDate today, int limit) {
      requestedToday = today;
      requestedLimit = limit;
      return results;
    }
  }
}
