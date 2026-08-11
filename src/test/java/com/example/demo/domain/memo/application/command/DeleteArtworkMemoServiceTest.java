package com.example.demo.domain.memo.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeleteArtworkMemoServiceTest {

  private final ArchiveWorkRepository archiveWorkRepository = mock(ArchiveWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeleteArtworkMemoService service =
      new DeleteArtworkMemoService(
          archiveWorkRepository, memoRepository, new MemoPermissionChecker());

  @Test
  void deletesExistingMemo() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    Memo memo = Memo.createForWork("감상", null, 10L);
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.of(memo));

    service.deleteArtworkMemo(7L, 10L);

    assertThat(memo.isDeleted()).isTrue();
  }

  @Test
  void rejectsArchiveWorkNotOwnedByUser() {
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArtworkMemo(7L, 10L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode()).isEqualTo(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND));
  }

  @Test
  void rejectsWhenNoMemoExists() {
    ArchiveWork archiveWork = archiveWork(10L, 7L);
    when(archiveWorkRepository.findById(10L)).thenReturn(Optional.of(archiveWork));
    when(memoRepository.findByArchiveWorkIdAndDeletedAtIsNull(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArtworkMemo(7L, 10L))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(MemoErrorCode.MEMO_NOT_FOUND));
  }

  private static ArchiveWork archiveWork(Long archiveWorkId, Long userId) {
    ArchiveWork archiveWork = ArchiveWork.create(100L, userId);
    ReflectionTestUtils.setField(archiveWork, "id", archiveWorkId);
    return archiveWork;
  }
}
