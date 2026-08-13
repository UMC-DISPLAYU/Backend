package com.example.demo.domain.personalartwork.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.domain.aggregate.PersonalArtwork;
import com.example.demo.domain.personalartwork.domain.entity.PersonalArtworkImage;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkLikeRepository;
import com.example.demo.domain.personalartwork.domain.repository.PersonalArtworkRepository;
import com.example.demo.domain.personalartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.personalartwork.domain.type.ArtworkType;
import com.example.demo.domain.personalartwork.domain.vo.UserId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PersonalArtworkQueryServiceTest {

  private final PersonalArtworkRepository personalArtworkRepository =
      mock(PersonalArtworkRepository.class);
  private final PersonalArtworkLikeRepository personalArtworkLikeRepository =
      mock(PersonalArtworkLikeRepository.class);
  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final PersonalArtworkQueryService service =
      new PersonalArtworkQueryService(
          personalArtworkRepository,
          personalArtworkLikeRepository,
          artistProfileRepository,
          archivePersonalWorkRepository);

  @Test
  void returnsArtistNameAndRequesterArchiveStatus() {
    PersonalArtwork artwork = personalArtwork();
    ArtistProfile artistProfile = mock(ArtistProfile.class);
    when(personalArtworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
    when(artistProfileRepository.findByUserId(10L)).thenReturn(Optional.of(artistProfile));
    when(artistProfile.getArtistName()).thenReturn("김마야");
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(20L, 1L))
        .thenReturn(Optional.of(ArchivePersonalWork.create(1L, 20L)));

    PersonalArtworkResult result = service.getPersonalArtworkDetail(1L, 20L);

    assertThat(result.artistName()).isEqualTo("김마야");
    assertThat(result.isArchived()).isTrue();
  }

  @Test
  void returnsNotArchivedWithoutRequester() {
    PersonalArtwork artwork = personalArtwork();
    when(personalArtworkRepository.findById(1L)).thenReturn(Optional.of(artwork));
    when(artistProfileRepository.findByUserId(10L)).thenReturn(Optional.empty());

    PersonalArtworkResult result = service.getPersonalArtworkDetail(1L, null);

    assertThat(result.artistName()).isNull();
    assertThat(result.isArchived()).isFalse();
    verifyNoInteractions(archivePersonalWorkRepository);
  }

  private static PersonalArtwork personalArtwork() {
    return PersonalArtwork.create(
        new UserId(10L),
        "작은 정원",
        "작품 설명",
        ArtworkType.COMPLEX,
        2026,
        "Mixed media",
        "100 x 100 x 150 cm",
        "빛과 그림자의 변화",
        List.of(
            new PersonalArtworkImage(
                null,
                "https://cdn.displayu.com/personal-artworks/garden.png",
                true,
                ArtworkImageType.ARTWORK,
                0,
                null,
                1200,
                1600)));
  }
}
