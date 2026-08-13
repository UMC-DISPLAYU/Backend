package com.example.demo.domain.memo.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeletePersonalWorkMemoServiceTest {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeletePersonalWorkMemoService service =
      new DeletePersonalWorkMemoService(
          archivePersonalWorkRepository, memoRepository, new MemoPermissionChecker());

  @Test
  void deletesExistingMemo() {
    ArchivePersonalWork archivePersonalWork = archivePersonalWork(10L, 7L);
    Memo memo = Memo.createForPersonalWork("감상", null, 10L);
    when(archivePersonalWorkRepository.findById(10L)).thenReturn(Optional.of(archivePersonalWork));
    when(memoRepository.findByArchivePersonalWorkIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(memo));

    service.deletePersonalWorkMemo(7L, 10L);

    assertThat(memo.isDeleted()).isTrue();
  }

  @Test
  void rejectsArchivePersonalWorkNotOwnedByUser() {
    when(archivePersonalWorkRepository.findById(10L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deletePersonalWorkMemo(7L, 10L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(MemoErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND));
  }

  @Test
  void rejectsWhenNoMemoExists() {
    ArchivePersonalWork archivePersonalWork = archivePersonalWork(10L, 7L);
    when(archivePersonalWorkRepository.findById(10L)).thenReturn(Optional.of(archivePersonalWork));
    when(memoRepository.findByArchivePersonalWorkIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deletePersonalWorkMemo(7L, 10L))
        .satisfies(
            exception -> assertThat(exception.errorCode()).isEqualTo(MemoErrorCode.MEMO_NOT_FOUND));
  }

  private static ArchivePersonalWork archivePersonalWork(Long archivePersonalWorkId, Long userId) {
    ArchivePersonalWork archivePersonalWork = ArchivePersonalWork.create(100L, userId);
    ReflectionTestUtils.setField(archivePersonalWork, "id", archivePersonalWorkId);
    return archivePersonalWork;
  }
}
