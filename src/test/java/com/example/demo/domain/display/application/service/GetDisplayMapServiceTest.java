package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetDisplayMapServiceTest {

  @Test
  void getDisplayMapUsesSizePlusOneAndReturnsNextCursor() {
    FakeDisplayMapQueryRepository queryRepository =
        new FakeDisplayMapQueryRepository(
            List.of(queryResult(30L), queryResult(20L), queryResult(10L)));
    GetDisplayMapService service = new GetDisplayMapService(queryRepository);

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
    GetDisplayMapService service = new GetDisplayMapService(queryRepository);

    DisplayMapResult result = service.getDisplayMap(query(2));

    assertThat(result.markers())
        .extracting(DisplayMapResult.MarkerResult::displayId)
        .containsExactly(20L, 10L);
    assertThat(result.pagination().hasNext()).isFalse();
    assertThat(result.pagination().nextCursor()).isNull();
  }

  private static DisplayMapQuery query(int size) {
    return new DisplayMapQuery(
        bd("37.4900"), bd("126.9000"), bd("37.5700"), bd("127.0000"), "디자인", 30L, size);
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
        bd("126.9248"));
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  private static class FakeDisplayMapQueryRepository implements DisplayMapQueryRepository {

    private final List<DisplayMapQueryResult> results;
    private int requestedLimit;

    private FakeDisplayMapQueryRepository(List<DisplayMapQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<DisplayMapQueryResult> findMarkers(DisplayMapQuery query, int limit) {
      requestedLimit = limit;
      return results;
    }
  }
}
