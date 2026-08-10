package com.example.demo.domain.memo.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.application.result.MemoResult;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

class UpsertExhibitionMemoServiceTest {

  private final ArchiveDisplayRepository archiveDisplayRepository =
      mock(ArchiveDisplayRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final UpsertExhibitionMemoService service =
      new UpsertExhibitionMemoService(archiveDisplayRepository, memoRepository);

  @Test
  void createsNewMemoWhenNoneExists() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    when(archiveDisplayRepository.findByIdAndUserId(10L, 7L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MemoResult result =
        service.upsertExhibitionMemo(
            new UpsertExhibitionMemoCommand(7L, 10L, "좋은 전시였다", LocalDate.of(2026, 8, 1)));

    assertThat(result.archiveDisplayId()).isEqualTo(10L);
    assertThat(result.content()).isEqualTo("좋은 전시였다");
    assertThat(result.visitDate()).isEqualTo(LocalDate.of(2026, 8, 1));
  }

  @Test
  void updatesExistingMemoContentWhenOneExists() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    Memo existing = Memo.createForDisplay("이전 감상", null, 10L);
    when(archiveDisplayRepository.findByIdAndUserId(10L, 7L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(existing));
    when(memoRepository.save(existing)).thenReturn(existing);

    MemoResult result =
        service.upsertExhibitionMemo(
            new UpsertExhibitionMemoCommand(7L, 10L, "수정된 감상", LocalDate.of(2026, 8, 2)));

    assertThat(result.content()).isEqualTo("수정된 감상");
    assertThat(result.visitDate()).isEqualTo(LocalDate.of(2026, 8, 2));
  }

  @Test
  void rejectsArchiveDisplayNotOwnedByUser() {
    when(archiveDisplayRepository.findByIdAndUserId(10L, 7L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () ->
                service.upsertExhibitionMemo(new UpsertExhibitionMemoCommand(7L, 10L, "감상", null)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));
    verify(memoRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentCreateWithUniqueConstraintViolation() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    when(archiveDisplayRepository.findByIdAndUserId(10L, 7L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException("Duplicate entry for UQ_MEMO_ACTIVE_ARCHIVEDISPLAY")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () ->
                service.upsertExhibitionMemo(new UpsertExhibitionMemoCommand(7L, 10L, "감상", null)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(MemoErrorCode.MEMO_CONCURRENT_WRITE_CONFLICT));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archiveDisplayRepository.findByIdAndUserId(10L, 7L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(
            () ->
                service.upsertExhibitionMemo(new UpsertExhibitionMemoCommand(7L, 10L, "감상", null)))
        .isSameAs(unrelated);
  }

  private static ArchiveDisplay archiveDisplay(Long archiveDisplayId, Long userId) {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(100L, userId);
    ReflectionTestUtils.setField(archiveDisplay, "id", archiveDisplayId);
    return archiveDisplay;
  }
}
