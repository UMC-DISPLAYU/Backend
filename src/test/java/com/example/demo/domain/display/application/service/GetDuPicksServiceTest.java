package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DuPickQuery;
import com.example.demo.domain.display.application.query.DuPickQueryRepository;
import com.example.demo.domain.display.application.query.DuPickQueryResult;
import com.example.demo.domain.display.application.result.DuPickResult;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetDuPicksServiceTest {

  @Test
  void getDuPicksUsesSizePlusOneAndReturnsNextCursor() {
    FakeDuPickQueryRepository queryRepository =
        new FakeDuPickQueryRepository(
            List.of(
                new DuPickQueryResult(
                    1L,
                    "THE ESSENCE IN MOTION",
                    "색과 형태, 우리가 마주한 순간들",
                    "https://cdn.displayu.com/home/du_pick_1.png",
                    "에디터 디유",
                    LocalDateTime.of(2026, 6, 30, 11, 0)),
                new DuPickQueryResult(
                    2L,
                    "시선이 머무는 각도",
                    "공간을 채우는 젊은 예술가 인터뷰",
                    "https://cdn.displayu.com/home/du_pick_2.png",
                    "에디터 디유",
                    LocalDateTime.of(2026, 6, 30, 11, 0))));
    GetDuPicksService service = new GetDuPicksService(queryRepository);

    DuPickResult result = service.getDuPicks(new DuPickQuery(null, 1));

    assertThat(queryRepository.requestedLimit).isEqualTo(2);
    assertThat(result.duPicks())
        .extracting(DuPickResult.DuPickItemResult::duPickId)
        .containsExactly(1L);
    assertThat(result.duPicks().getFirst().createdAt()).hasToString("2026-06-30");
    assertThat(result.pagination().nextCursor()).isEqualTo(1L);
    assertThat(result.pagination().size()).isEqualTo(1);
    assertThat(result.pagination().hasNext()).isTrue();
  }

  private static class FakeDuPickQueryRepository implements DuPickQueryRepository {

    private final List<DuPickQueryResult> results;
    private DuPickQuery requestedQuery;
    private int requestedLimit;

    private FakeDuPickQueryRepository(List<DuPickQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<DuPickQueryResult> findDuPicks(DuPickQuery query, int limit) {
      requestedQuery = query;
      requestedLimit = limit;
      return results;
    }
  }
}
