package com.example.demo.domain.personalartwork.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesByUserIdUseCase;
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

  private final GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase =
      mock(GetArtistProfileSummariesByUserIdUseCase.class);

  private final GetPersonalArtworkSummariesService service =
      new GetPersonalArtworkSummariesService(
          personalArtworkRepository, getArtistProfileSummariesByUserIdUseCase);

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

  @Test
  void getPersonalArtworkSummariesFillsArtistNameFromArtistProfile() {
    when(personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(List.of(1L)))
        .thenReturn(List.of(artwork("작은 정원", 1L)));
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(1L)))
        .thenReturn(List.of(artistProfile(1L, "김마야")));

    List<PersonalArtworkSummaryResult> summaries = service.getPersonalArtworkSummaries(List.of(1L));

    assertThat(summaries.getFirst().artistName()).isEqualTo("김마야");
  }

  @Test
  void getPersonalArtworkSummariesLooksUpEachArtistOnlyOnce() {
    when(personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(List.of(1L, 2L, 3L)))
        .thenReturn(List.of(artwork("작가1 작품A", 1L), artwork("작가1 작품B", 1L), artwork("작가2 작품", 2L)));
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(
            List.of(1L, 2L)))
        .thenReturn(List.of(artistProfile(1L, "김마야"), artistProfile(2L, "이정우")));

    List<PersonalArtworkSummaryResult> summaries =
        service.getPersonalArtworkSummaries(List.of(1L, 2L, 3L));

    // 작가가 겹치면 userId를 모아 한 번만 조회한다.
    assertThat(summaries)
        .extracting(PersonalArtworkSummaryResult::artistName)
        .containsExactly("김마야", "김마야", "이정우");
  }

  @Test
  void getPersonalArtworkSummariesLeavesArtistNameNullWhenProfileIsMissing() {
    when(personalArtworkRepository.findAllByIdInAndDeletedAtIsNull(List.of(1L)))
        .thenReturn(List.of(artwork("작은 정원", 1L)));
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(1L)))
        .thenReturn(List.of());

    List<PersonalArtworkSummaryResult> summaries = service.getPersonalArtworkSummaries(List.of(1L));

    assertThat(summaries.getFirst().artistName()).isNull();
  }

  private static ArtistProfileSummaryResult artistProfile(Long userId, String artistName) {
    return new ArtistProfileSummaryResult(userId, userId, artistName, null, List.of());
  }

  private static PersonalArtwork artwork(String artworkName) {
    return artwork(artworkName, 1L);
  }

  private static PersonalArtwork artwork(String artworkName, Long ownerUserId) {
    return PersonalArtwork.create(
        new UserId(ownerUserId),
        artworkName,
        "content",
        List.of(ArtworkType.COMPLEX),
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
