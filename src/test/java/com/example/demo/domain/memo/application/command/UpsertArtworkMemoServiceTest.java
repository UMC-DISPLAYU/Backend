package com.example.demo.domain.memo.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
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

class UpsertArtworkMemoServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final UpsertArtworkMemoService service =
      new UpsertArtworkMemoService(
          archiveWorkRepository, memoRepository, new MemoPermissionChecker());

  @Test
  void createsNewMemoWhenNoneExists() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MemoResult result =
        service.upsertArtworkMemo(
            new UpsertArtworkMemoCommand(7L, 10L, "인상 깊었다", LocalDate.of(2026, 8, 1)));

    assertThat(result.archiveWorkId()).isEqualTo(10L);
    assertThat(result.content()).isEqualTo("인상 깊었다");
    assertThat(result.visitDate()).isEqualTo(LocalDate.of(2026, 8, 1));
  }

  @Test
  void updatesExistingMemoContentWhenOneExists() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    Memo existing = Memo.createForWork("이전 감상", null, 10L);
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(existing));
    when(memoRepository.save(existing)).thenReturn(existing);

    MemoResult result =
        service.upsertArtworkMemo(
            new UpsertArtworkMemoCommand(7L, 10L, "수정된 감상", LocalDate.of(2026, 8, 2)));

    assertThat(result.content()).isEqualTo("수정된 감상");
    assertThat(result.visitDate()).isEqualTo(LocalDate.of(2026, 8, 2));
  }

  @Test
  void rejectsArchiveWorkNotOwnedByUser() {
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () -> service.upsertArtworkMemo(new UpsertArtworkMemoCommand(7L, 10L, "감상", null)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND));
    verify(memoRepository, never()).save(any());
  }

  @Test
  void rejectsConcurrentCreateWithUniqueConstraintViolation() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class)))
        .thenThrow(
            new DataIntegrityViolationException(
                "duplicate",
                new RuntimeException("Duplicate entry for UQ_MEMO_ACTIVE_ARCHIVEWORK")));

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () -> service.upsertArtworkMemo(new UpsertArtworkMemoCommand(7L, 10L, "감상", null)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(MemoErrorCode.MEMO_CONCURRENT_WRITE_CONFLICT));
  }

  @Test
  void rethrowsUnrelatedDataIntegrityViolation() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    DataIntegrityViolationException unrelated =
        new DataIntegrityViolationException(
            "fk violation", new RuntimeException("foreign key constraint fails"));
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());
    when(memoRepository.save(any(Memo.class))).thenThrow(unrelated);

    assertThatExceptionOfType(DataIntegrityViolationException.class)
        .isThrownBy(
            () -> service.upsertArtworkMemo(new UpsertArtworkMemoCommand(7L, 10L, "감상", null)))
        .isSameAs(unrelated);
  }

  private static ArchiveWork archiveWork(Long archiveWorkId, Long userId) {
    ArchiveWork archiveWork = ArchiveWork.create(100L, userId);
    ReflectionTestUtils.setField(archiveWork, "id", archiveWorkId);
    return archiveWork;
  }
}
