package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplaySummaryQueryRepository;
import com.example.demo.domain.display.application.query.DisplaySummaryQueryResult;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetDisplaySummariesServiceTest {

  @Test
  void getDisplaySummariesReturnsMappedResults() {
    FakeDisplaySummaryQueryRepository queryRepository =
        new FakeDisplaySummaryQueryRepository(
            List.of(
                new DisplaySummaryQueryResult(
                    1L,
                    "전시",
                    "organization",
                    "department",
                    "place",
                    LocalDate.of(2026, 7, 1),
                    LocalDate.of(2026, 7, 10),
                    "https://cdn.displayu.com/posters/main.png")));
    GetDisplaySummariesService service = new GetDisplaySummariesService(queryRepository);

    List<DisplaySummaryResult> results = service.getDisplaySummaries(List.of(1L, 2L));

    assertThat(queryRepository.requestedDisplayIds).containsExactly(1L, 2L);
    assertThat(results).hasSize(1);
    assertThat(results.getFirst().displayId()).isEqualTo(1L);
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void getDisplaySummariesSkipsRepositoryWhenIdsAreEmpty() {
    FakeDisplaySummaryQueryRepository queryRepository =
        new FakeDisplaySummaryQueryRepository(List.of());
    GetDisplaySummariesService service = new GetDisplaySummariesService(queryRepository);

    List<DisplaySummaryResult> results = service.getDisplaySummaries(List.of());

    assertThat(results).isEmpty();
    assertThat(queryRepository.requestedDisplayIds).isNull();
  }

  private static class FakeDisplaySummaryQueryRepository implements DisplaySummaryQueryRepository {

    private final List<DisplaySummaryQueryResult> results;
    private List<Long> requestedDisplayIds;

    private FakeDisplaySummaryQueryRepository(List<DisplaySummaryQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<DisplaySummaryQueryResult> findByDisplayIdIn(List<Long> displayIds) {
      requestedDisplayIds = displayIds;
      return results;
    }
  }
}
