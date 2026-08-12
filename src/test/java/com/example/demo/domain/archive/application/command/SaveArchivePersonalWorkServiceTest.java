package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkSummariesUseCase;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class SaveArchivePersonalWorkServiceTest {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase =
      mock(GetPersonalArtworkSummariesUseCase.class);
  private final SaveArchivePersonalWorkService service =
      new SaveArchivePersonalWorkService(
          archivePersonalWorkRepository, getPersonalArtworkSummariesUseCase);

  @Test
  void savesArchivePersonalWorkWhenPersonalArtworkExistsAndNotAlreadyArchived() {
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of(personalArtworkSummary(300L)));
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.empty());

    ArchivePersonalWorkToggleResult result =
        service.saveArchivePersonalWork(new SaveArchivePersonalWorkCommand(7L, 300L));

    assertThat(result.personalArtworkId()).isEqualTo(300L);
    assertThat(result.isArchived()).isTrue();
    verify(archivePersonalWorkRepository).save(any(ArchivePersonalWork.class));
  }

  @Test
  void rejectsWhenPersonalArtworkDoesNotExist() {
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () -> service.saveArchivePersonalWork(new SaveArchivePersonalWorkCommand(7L, 300L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.PERSONAL_ARTWORK_NOT_FOUND));
    verify(archivePersonalWorkRepository, never()).save(any());
  }

  @Test
  void rejectsWhenAlreadyArchived() {
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of(personalArtworkSummary(300L)));
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.of(ArchivePersonalWork.create(300L, 7L)));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () -> service.saveArchivePersonalWork(new SaveArchivePersonalWorkCommand(7L, 300L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_PERSONAL_WORK));
    verify(archivePersonalWorkRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentSaveWithUniqueConstraintViolation() {
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of(personalArtworkSummary(300L)));
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.empty());
    when(archivePersonalWorkRepository.save(any(ArchivePersonalWork.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException(
                    "Duplicate entry for UQ_ARCHIVEPERSONALWORK_ACTIVE_USER_PERSONALARTWORK")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () -> service.saveArchivePersonalWork(new SaveArchivePersonalWorkCommand(7L, 300L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_PERSONAL_WORK));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    when(getPersonalArtworkSummariesUseCase.getPersonalArtworkSummaries(List.of(300L)))
        .thenReturn(List.of(personalArtworkSummary(300L)));
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.empty());
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archivePersonalWorkRepository.save(any(ArchivePersonalWork.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(
            () -> service.saveArchivePersonalWork(new SaveArchivePersonalWorkCommand(7L, 300L)))
        .isSameAs(unrelated);
  }

  private static PersonalArtworkSummaryResult personalArtworkSummary(Long personalArtworkId) {
    return new PersonalArtworkSummaryResult(
        personalArtworkId,
        "작은 정원",
        "https://cdn.displayu.com/personal-artworks/garden.png",
        "COMPLEX",
        LocalDateTime.now());
  }
}
