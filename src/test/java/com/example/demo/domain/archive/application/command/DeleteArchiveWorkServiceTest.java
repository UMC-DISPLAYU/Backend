package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeleteArchiveWorkServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeleteArchiveWorkService service =
      new DeleteArchiveWorkService(
          archiveWorkRepository, memoRepository, new ArchivePermissionChecker());

  @Test
  void softDeletesAssociatedMemoAndArchiveWork() {
    ArchiveWork archiveWork = archiveWork(10L, 100L, 7L);
    Memo memo = Memo.createForWork("메모", null, 10L);
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 100L))
        .thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(memo));

    ArchiveWorkToggleResult result = service.deleteArchiveWork(7L, 100L);

    assertThat(result.displayArtworkId()).isEqualTo(100L);
    assertThat(result.isArchived()).isFalse();
    assertThat(memo.isDeleted()).isTrue();
    assertThat(archiveWork.isDeleted()).isTrue();
    verify(archiveWorkRepository, never()).delete(archiveWork);
  }

  @Test
  void softDeletesArchiveWorkWhenNoActiveMemoExists() {
    ArchiveWork archiveWork = archiveWork(10L, 100L, 7L);
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 100L))
        .thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

    service.deleteArchiveWork(7L, 100L);

    assertThat(archiveWork.isDeleted()).isTrue();
    verify(archiveWorkRepository, never()).delete(archiveWork);
  }

  @Test
  void rejectsWhenArchivedWorkDoesNotExist() {
    when(archiveWorkRepository.findByUserIdAndDisplayArtworkId(7L, 100L))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArchiveWork(7L, 100L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ARCHIVE_WORK_NOT_FOUND));
  }

  private static ArchiveWork archiveWork(Long archiveWorkId, Long displayArtworkId, Long userId) {
    ArchiveWork archiveWork = ArchiveWork.create(displayArtworkId, userId);
    ReflectionTestUtils.setField(archiveWork, "id", archiveWorkId);
    return archiveWork;
  }
}
