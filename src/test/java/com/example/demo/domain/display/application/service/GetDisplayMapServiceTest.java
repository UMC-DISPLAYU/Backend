package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetDisplayMapServiceTest {
  private static final Clock CLOCK =
      Clock.fixed(Instant.parse("2026-05-20T00:00:00Z"), ZoneOffset.UTC);

  @Test
  void getDisplayMapUsesSizePlusOneAndReturnsNextCursor() {
    FakeDisplayMapQueryRepository queryRepository =
        new FakeDisplayMapQueryRepository(
            List.of(queryResult(30L), queryResult(20L), queryResult(10L)));
    GetDisplayMapService service = new GetDisplayMapService(queryRepository, CLOCK);

    DisplayMapResult result = service.getDisplayMap(query(2));

    assertThat(queryRepository.requestedLimit).isEqualTo(3);
    assertThat(result.markers())
        .extracting(DisplayMapResult.MarkerResult::displayId)
        .containsExactly(30L, 20L);
    assertThat(result.pagination().hasNext()).isTrue();
    assertThat(result.pagination().nextCursor()).isEqualTo(20L);
    assertThat(result.pagination().size()).isEqualTo(2);
  }

  @Test
  void getDisplayMapReturnsNullNextCursorOnLastPage() {
    FakeDisplayMapQueryRepository queryRepository =
        new FakeDisplayMapQueryRepository(List.of(queryResult(20L), queryResult(10L)));
    GetDisplayMapService service = new GetDisplayMapService(queryRepository, CLOCK);

    DisplayMapResult result = service.getDisplayMap(query(2));

    assertThat(result.markers())
        .extracting(DisplayMapResult.MarkerResult::displayId)
        .containsExactly(20L, 10L);
    assertThat(result.pagination().hasNext()).isFalse();
    assertThat(result.pagination().nextCursor()).isNull();
  }

  @Test
  void allModeReturnsEveryMarkerWithActualSizeAndNoCursor() {
    var queryRepository =
        new FakeDisplayMapQueryRepository(
            List.of(queryResult(30L), queryResult(20L), queryResult(10L)));
    var service = new GetDisplayMapService(queryRepository, CLOCK);
    var query =
        new DisplayMapQuery(
            bd("37.4900"), bd("126.9000"), bd("37.5700"), bd("127.0000"), null, null, 1, true);
    var result = service.getDisplayMap(query);
    assertThat(result.markers()).hasSize(3);
    assertThat(result.pagination().size()).isEqualTo(3);
    assertThat(result.pagination().hasNext()).isFalse();
    assertThat(result.pagination().nextCursor()).isNull();
    assertThat(queryRepository.requestedToday).isEqualTo(LocalDate.now(CLOCK));
  }

  private static DisplayMapQuery query(int size) {
    return new DisplayMapQuery(
        bd("37.4900"), bd("126.9000"), bd("37.5700"), bd("127.0000"), "디자인", 30L, size, false);
  }

  private static DisplayMapQueryResult queryResult(Long id) {
    return new DisplayMapQueryResult(
        id,
        "전시 " + id,
        LocalDate.of(2026, 5, 20),
        LocalDate.of(2026, 5, 28),
        "장소 " + id,
        "https://cdn.displayu.com/posters/main.png",
        "organization",
        "department",
        bd("37.5513"),
        bd("126.9248"),
        null);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  private static class FakeDisplayMapQueryRepository implements DisplayMapQueryRepository {

    private final List<DisplayMapQueryResult> results;
    private int requestedLimit;
    private LocalDate requestedToday;

    private FakeDisplayMapQueryRepository(List<DisplayMapQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<DisplayMapQueryResult> findMarkers(
        DisplayMapQuery query, LocalDate today, int limit) {
      requestedLimit = limit;
      requestedToday = today;
      return results;
    }
  }
}
