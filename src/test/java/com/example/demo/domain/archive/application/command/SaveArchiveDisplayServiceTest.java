package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.display.application.result.DisplaySummaryResult;
import com.example.demo.domain.display.application.usecase.GetDisplaySummariesUseCase;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class SaveArchiveDisplayServiceTest {

  private final ArchiveDisplayRepository archiveDisplayRepository =
      mock(ArchiveDisplayRepository.class);
  private final GetDisplaySummariesUseCase getDisplaySummariesUseCase =
      mock(GetDisplaySummariesUseCase.class);
  private final SaveArchiveDisplayService service =
      new SaveArchiveDisplayService(archiveDisplayRepository, getDisplaySummariesUseCase);

  @Test
  void savesArchiveDisplayWhenDisplayExistsAndNotAlreadyArchived() {
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(displaySummary(100L)));
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L)).thenReturn(Optional.empty());

    ArchiveDisplayToggleResult result =
        service.saveArchiveDisplay(new SaveArchiveDisplayCommand(7L, 100L));

    assertThat(result.displayId()).isEqualTo(100L);
    assertThat(result.isArchived()).isTrue();
    verify(archiveDisplayRepository).save(any(ArchiveDisplay.class));
  }

  @Test
  void rejectsWhenDisplayDoesNotExist() {
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L))).thenReturn(List.of());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveDisplay(new SaveArchiveDisplayCommand(7L, 100L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(ArchiveErrorCode.DISPLAY_NOT_FOUND));
    verify(archiveDisplayRepository, never()).save(any());
  }

  @Test
  void rejectsWhenAlreadyArchived() {
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(displaySummary(100L)));
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L))
        .thenReturn(Optional.of(ArchiveDisplay.create(100L, 7L)));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveDisplay(new SaveArchiveDisplayCommand(7L, 100L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_DISPLAY));
    verify(archiveDisplayRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentSaveWithUniqueConstraintViolation() {
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(displaySummary(100L)));
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L)).thenReturn(Optional.empty());
    when(archiveDisplayRepository.save(any(ArchiveDisplay.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException("Duplicate entry for UQ_ARCHIVEDISPLAY_USER_DISPLAY")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.saveArchiveDisplay(new SaveArchiveDisplayCommand(7L, 100L)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ALREADY_ARCHIVED_DISPLAY));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    when(getDisplaySummariesUseCase.getDisplaySummaries(List.of(100L)))
        .thenReturn(List.of(displaySummary(100L)));
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L)).thenReturn(Optional.empty());
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archiveDisplayRepository.save(any(ArchiveDisplay.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(() -> service.saveArchiveDisplay(new SaveArchiveDisplayCommand(7L, 100L)))
        .isSameAs(unrelated);
  }

  private static DisplaySummaryResult displaySummary(Long displayId) {
    return new DisplaySummaryResult(
        displayId,
        "디자인 졸업전시",
        "디유대학교",
        "시각디자인과",
        "디유갤러리",
        LocalDate.now().minusDays(1),
        LocalDate.now().plusDays(10),
        "https://cdn.displayu.com/posters/main.png");
  }
}
