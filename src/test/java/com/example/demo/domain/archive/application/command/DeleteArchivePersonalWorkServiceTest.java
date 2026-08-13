package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DeleteArchivePersonalWorkServiceTest {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final MemoRepository memoRepository = mock(MemoRepository.class);
  private final DeleteArchivePersonalWorkService service =
      new DeleteArchivePersonalWorkService(archivePersonalWorkRepository, memoRepository);

  @Test
  void softDeletesAssociatedMemoAndArchivePersonalWork() {
    ArchivePersonalWork archivePersonalWork = archivePersonalWork(10L, 300L, 7L);
    Memo memo = Memo.createForPersonalWork("메모", null, 10L);
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.of(archivePersonalWork));
    when(memoRepository.findByArchivePersonalWorkIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.of(memo));

    ArchivePersonalWorkToggleResult result = service.deleteArchivePersonalWork(7L, 300L);

    assertThat(result.personalArtworkId()).isEqualTo(300L);
    assertThat(result.isArchived()).isFalse();
    assertThat(memo.isDeleted()).isTrue();
    assertThat(archivePersonalWork.isDeleted()).isTrue();
    verify(archivePersonalWorkRepository, never()).delete(archivePersonalWork);
  }

  @Test
  void softDeletesArchivePersonalWorkWhenNoActiveMemoExists() {
    ArchivePersonalWork archivePersonalWork = archivePersonalWork(10L, 300L, 7L);
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.of(archivePersonalWork));
    when(memoRepository.findByArchivePersonalWorkIdAndDeletedAtIsNull(10L))
        .thenReturn(Optional.empty());

    ArchivePersonalWorkToggleResult result = service.deleteArchivePersonalWork(7L, 300L);

    assertThat(result.personalArtworkId()).isEqualTo(300L);
    assertThat(result.isArchived()).isFalse();
    assertThat(archivePersonalWork.isDeleted()).isTrue();
    verify(archivePersonalWorkRepository, never()).delete(archivePersonalWork);
  }

  private static ArchivePersonalWork archivePersonalWork(
      Long archivePersonalWorkId, Long personalArtworkId, Long userId) {
    ArchivePersonalWork archivePersonalWork = ArchivePersonalWork.create(personalArtworkId, userId);
    ReflectionTestUtils.setField(archivePersonalWork, "id", archivePersonalWorkId);
    return archivePersonalWork;
  }

  @Test
  void rejectsWhenArchivedPersonalWorkDoesNotExist() {
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArchivePersonalWork(7L, 300L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND));
  }
}
