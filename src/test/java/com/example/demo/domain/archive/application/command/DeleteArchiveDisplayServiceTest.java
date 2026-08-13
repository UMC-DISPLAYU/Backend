package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeleteArchiveDisplayServiceTest {

  private final ArchiveDisplayRepository archiveDisplayRepository =
      mock(ArchiveDisplayRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeleteArchiveDisplayService service =
      new DeleteArchiveDisplayService(
          archiveDisplayRepository, memoRepository, new ArchivePermissionChecker());

  @Test
  void softDeletesAssociatedMemoAndArchiveDisplay() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 100L, 7L);
    Memo memo = Memo.createForDisplay("메모", null, 10L);
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(memo));

    ArchiveDisplayToggleResult result = service.deleteArchiveDisplay(7L, 100L);

    assertThat(result.displayId()).isEqualTo(100L);
    assertThat(result.isArchived()).isFalse();
    assertThat(memo.isDeleted()).isTrue();
    assertThat(archiveDisplay.isDeleted()).isTrue();
    verify(archiveDisplayRepository, never()).delete(archiveDisplay);
  }

  @Test
  void softDeletesArchiveDisplayWhenNoActiveMemoExists() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 100L, 7L);
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L))
        .thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

    service.deleteArchiveDisplay(7L, 100L);

    assertThat(archiveDisplay.isDeleted()).isTrue();
    verify(archiveDisplayRepository, never()).delete(archiveDisplay);
  }

  @Test
  void rejectsWhenArchivedDisplayDoesNotExist() {
    when(archiveDisplayRepository.findByUserIdAndDisplayId(7L, 100L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArchiveDisplay(7L, 100L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));
  }

  private static ArchiveDisplay archiveDisplay(Long archiveDisplayId, Long displayId, Long userId) {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(displayId, userId);
    ReflectionTestUtils.setField(archiveDisplay, "id", archiveDisplayId);
    return archiveDisplay;
  }
}
