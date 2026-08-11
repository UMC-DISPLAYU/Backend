package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryRepository;
import com.example.demo.domain.artist.application.query.ArtistProfileSummaryQueryResult;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetArtistProfileSummariesByUserIdServiceTest {

  private final ArtistProfileSummaryQueryRepository queryRepository =
      mock(ArtistProfileSummaryQueryRepository.class);
  private final GetArtistProfileSummariesByUserIdService service =
      new GetArtistProfileSummariesByUserIdService(queryRepository);

  @Test
  void emptyUserIdsReturnEmptyResultWithoutQuery() {
    assertThat(service.getArtistProfileSummariesByUserId(List.of())).isEmpty();

    verify(queryRepository, never()).findByUserIdIn(List.of());
  }

  @Test
  void artistProfileSummariesAreReturnedByUserIds() {
    List<Long> userIds = List.of(1L, 2L);
    when(queryRepository.findByUserIdIn(userIds))
        .thenReturn(
            List.of(
                new ArtistProfileSummaryQueryResult(
                    10L, 1L, "artist", "profile.jpg", List.of(ActivityCategory.DESIGN))));

    var results = service.getArtistProfileSummariesByUserId(userIds);

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().artistProfileId()).isEqualTo(10L);
    assertThat(results.getFirst().userId()).isEqualTo(1L);
    verify(queryRepository).findByUserIdIn(userIds);
  }
}
