package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.displayartwork.application.result.ArtworkSummaryResult;
import com.example.demo.domain.displayartwork.application.usecase.GetArtworkSummariesUseCase;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class SaveArchiveWorkServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final GetArtworkSummariesUseCase getArtworkSummariesUseCase =
      mock(GetArtworkSummariesUseCase.class);
  private final SaveArchiveWorkService service =
      new SaveArchiveWorkService(archiveWorkRepository, getArtworkSummariesUseCase);

  @Test
  void savesArchiveWorkWhenArtworkExistsAndNotAlreadyArchived() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(200L)))
        .thenReturn(List.of(artworkSummary(200L)));
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 200L))
        .thenReturn(Optional.empty());

    ArchiveWorkToggleResult result = service.saveArchiveWork(new SaveArchiveWorkCommand(7L, 200L));

    assertThat(result.displayArtworkId()).isEqualTo(200L);
    assertThat(result.isArchived()).isTrue();
    verify(archiveWorkRepository).save(any(ArchiveWork.class));
  }

  @Test
  void rejectsWhenDisplayArtworkDoesNotExist() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(200L))).thenReturn(List.of());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveWork(new SaveArchiveWorkCommand(7L, 200L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.DISPLAY_ARTWORK_NOT_FOUND));
    verify(archiveWorkRepository, never()).save(any());
  }

  @Test
  void rejectsWhenAlreadyArchived() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(200L)))
        .thenReturn(List.of(artworkSummary(200L)));
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 200L))
        .thenReturn(Optional.of(ArchiveWork.create(200L, 7L)));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveWork(new SaveArchiveWorkCommand(7L, 200L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_WORK));
    verify(archiveWorkRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentSaveWithUniqueConstraintViolation() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(200L)))
        .thenReturn(List.of(artworkSummary(200L)));
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 200L))
        .thenReturn(Optional.empty());
    when(archiveWorkRepository.save(any(ArchiveWork.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException("Duplicate entry for UQ_ARCHIVEWORK_USER_DISPLAYARTWORK")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveWork(new SaveArchiveWorkCommand(7L, 200L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_WORK));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    when(getArtworkSummariesUseCase.getArtworkSummaries(List.of(200L)))
        .thenReturn(List.of(artworkSummary(200L)));
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 200L))
        .thenReturn(Optional.empty());
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archiveWorkRepository.save(any(ArchiveWork.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.saveArchiveWork(new SaveArchiveWorkCommand(7L, 200L)))
        .isSameAs(unrelated);
  }

  private static ArtworkSummaryResult artworkSummary(Long displayArtworkId) {
    return new ArtworkSummaryResult(
        displayArtworkId, "FORM 2026", "고상준", "https://cdn.displayu.co.kr/artworks/1/thumb.jpg");
  }
}
