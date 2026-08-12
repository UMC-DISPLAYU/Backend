package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesByUserIdUseCase;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

class SaveArchiveArtistServiceTest {

  private final ArchiveArtistRepository archiveArtistRepository =
      mock(ArchiveArtistRepository.class);
  private final GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase =
      mock(GetArtistProfileSummariesByUserIdUseCase.class);
  private final SaveArchiveArtistService service =
      new SaveArchiveArtistService(
          archiveArtistRepository, getArtistProfileSummariesByUserIdUseCase);

  @Test
  void savesArchiveArtistWhenArtistProfileExistsAndNotAlreadyArchived() {
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(30L)))
        .thenReturn(List.of(artistProfileSummary(18L, 30L)));
    when(archiveArtistRepository.findByUserIdAndArtistProfileId(7L, 18L))
        .thenReturn(Optional.empty());

    ArchiveArtistToggleResult result =
        service.saveArchiveArtist(new SaveArchiveArtistCommand(7L, 30L));

    assertThat(result.artistUserId()).isEqualTo(30L);
    assertThat(result.isArchived()).isTrue();
    ArgumentCaptor<ArchiveArtist> captor = ArgumentCaptor.forClass(ArchiveArtist.class);
    verify(archiveArtistRepository).save(captor.capture());
    ArchiveArtist saved = captor.getValue();
    assertThat(saved.getArtistProfileId()).isEqualTo(18L);
    assertThat(saved.getArtistUserId()).isEqualTo(30L);
    assertThat(saved.getUserId()).isEqualTo(7L);
  }

  @Test
  void rejectsWhenArtistProfileDoesNotExist() {
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(30L)))
        .thenReturn(List.of());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveArtist(new SaveArchiveArtistCommand(7L, 30L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ARTIST_PROFILE_NOT_FOUND));
    verify(archiveArtistRepository, never()).save(any());
  }

  @Test
  void rejectsWhenAlreadyArchived() {
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(30L)))
        .thenReturn(List.of(artistProfileSummary(18L, 30L)));
    when(archiveArtistRepository.findByUserIdAndArtistProfileId(7L, 18L))
        .thenReturn(Optional.of(ArchiveArtist.create(18L, 30L, 7L)));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveArtist(new SaveArchiveArtistCommand(7L, 30L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST));
    verify(archiveArtistRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentSaveWithUniqueConstraintViolation() {
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(30L)))
        .thenReturn(List.of(artistProfileSummary(18L, 30L)));
    when(archiveArtistRepository.findByUserIdAndArtistProfileId(7L, 18L))
        .thenReturn(Optional.empty());
    when(archiveArtistRepository.save(any(ArchiveArtist.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException("Duplicate entry for UQ_ARCHIVEARTIST_USER_ARTISTPROFILE")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveArtist(new SaveArchiveArtistCommand(7L, 30L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    when(getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(List.of(30L)))
        .thenReturn(List.of(artistProfileSummary(18L, 30L)));
    when(archiveArtistRepository.findByUserIdAndArtistProfileId(7L, 18L))
        .thenReturn(Optional.empty());
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archiveArtistRepository.save(any(ArchiveArtist.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.saveArchiveArtist(new SaveArchiveArtistCommand(7L, 30L)))
        .isSameAs(unrelated);
  }

  private static ArtistProfileSummaryResult artistProfileSummary(
      Long artistProfileId, Long userId) {
    return new ArtistProfileSummaryResult(
        artistProfileId,
        userId,
        "ArtistBeanie",
        "https://cdn.displayu.com/artists/profile.png",
        List.of(ActivityCategory.PHOTOGRAPHY));
  }
}
