package com.example.demo.domain.memo.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeleteExhibitionMemoServiceTest {

  private final ArchiveDisplayRepository archiveDisplayRepository =
      mock(ArchiveDisplayRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeleteExhibitionMemoService service =
      new DeleteExhibitionMemoService(
          archiveDisplayRepository, memoRepository, new MemoPermissionChecker());

  @Test
  void deletesExistingMemo() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    Memo memo = Memo.createForDisplay("감상", null, 10L);
    when(archiveDisplayRepository.findById(10L)).thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(memo));

    service.deleteExhibitionMemo(7L, 10L);

    assertThat(memo.isDeleted()).isTrue();
  }

  @Test
  void rejectsArchiveDisplayNotOwnedByUser() {
    when(archiveDisplayRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteExhibitionMemo(7L, 10L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));
  }

  @Test
  void rejectsWhenNoMemoExists() {
    ArchiveDisplay archiveDisplay = archiveDisplay(10L, 7L);
    when(archiveDisplayRepository.findById(10L)).thenReturn(Optional.of(archiveDisplay));
    when(memoRepository.findByArchiveDisplayIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteExhibitionMemo(7L, 10L))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(MemoErrorCode.MEMO_NOT_FOUND));
  }

  private static ArchiveDisplay archiveDisplay(Long archiveDisplayId, Long userId) {
    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(100L, userId);
    ReflectionTestUtils.setField(archiveDisplay, "id", archiveDisplayId);
    return archiveDisplay;
  }
}
