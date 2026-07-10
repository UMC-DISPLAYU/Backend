package com.example.demo.domain.display.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetRandomGraduationDisplaysServiceTest {

  @Test
  void getRandomGraduationDisplaysRequestsGivenSizeAndCalculatesDayLeft() {
    LocalDate today = LocalDate.now();
    FakeGraduationDisplayQueryRepository queryRepository =
        new FakeGraduationDisplayQueryRepository(
            List.of(queryResult(1L, "졸업 전시", today.plusDays(4))));
    GetRandomGraduationDisplaysService service =
        new GetRandomGraduationDisplaysService(queryRepository);

    ClosingSoonDisplayResult result = service.getRandomGraduationDisplays(5);

    assertThat(queryRepository.requestedSize).isEqualTo(5);
    assertThat(result.exhibitions()).hasSize(1);
    assertThat(result.exhibitions().getFirst().dayLeft()).isEqualTo(4);
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

  private static class FakeGraduationDisplayQueryRepository
      implements GraduationDisplayQueryRepository {

    private final List<ClosingSoonDisplayQueryResult> results;
    private int requestedSize;

    private FakeGraduationDisplayQueryRepository(List<ClosingSoonDisplayQueryResult> results) {
      this.results = results;
    }

    @Override
    public List<ClosingSoonDisplayQueryResult> findRandomGraduationDisplays(int size) {
      requestedSize = size;
      return results;
    }
  }
}
