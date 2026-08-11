package com.example.demo.domain.personalartwork.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import java.util.List;
import org.junit.jupiter.api.Test;

class GetPersonalArtworkSummariesServiceTest {

  private final PersonalArtworkRepository personalArtworkRepository =
      mock(PersonalArtworkRepository.class);

  private final GetPersonalArtworkSummariesService service =
      new GetPersonalArtworkSummariesService(personalArtworkRepository);

  @Test
  void getPersonalArtworkSummariesReturnsEmptyWithoutQueryingWhenIdsAreEmpty() {
    assertThat(service.getPersonalArtworkSummaries(List.of())).isEmpty();

    verify(personalArtworkRepository, never()).findAllByIdInAndDeletedAtIsNull(anyList());
  }

  @Test
  void getPersonalArtworkSummariesMapsArtworksToSummaries() {
    when(personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(List.of(1L)))
        .thenReturn(List.of(artwork("작은 정원")));

    List<PersonalArtworkSummaryResult> summaries = service.getPersonalArtworkSummaries(List.of(1L));

    assertThat(summaries)
        .extracting(PersonalArtworkSummaryResult::artworkName, PersonalArtworkSummaryResult::type)
        .containsExactly(org.assertj.core.api.Assertions.tuple("작은 정원", "COMPLEX"));
    assertThat(summaries.getFirst().thumbnailUrl())
        .isEqualTo("https://cdn.displayu.com/personal-artworks/garden.png");
  }

  private static PersonalArtwork artwork(String artworkName) {
    return PersonalArtwork.create(
        new UserId(1L),
        artworkName,
        "content",
        ArtworkType.COMPLEX,
        2026,
        "Mixed media",
        "100 x 100 cm",
        "point",
        List.of(
            new PersonalArtworkImage(
                null,
                "https://cdn.displayu.com/personal-artworks/garden.png",
                true,
                ArtworkImageType.ARTWORK,
                1,
                "대표 이미지",
                1200,
                1600)));
  }
}
