package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeleteArchivePersonalWorkServiceTest {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository =
      mock(ArchivePersonalWorkRepository.class);
  private final DeleteArchivePersonalWorkService service =
      new DeleteArchivePersonalWorkService(archivePersonalWorkRepository);

  @Test
  void deletesArchivePersonalWorkWhenItExists() {
    ArchivePersonalWork archivePersonalWork = ArchivePersonalWork.create(300L, 7L);
    when(archivePersonalWorkRepository.findByUserIdAndPersonalArtworkId(7L, 300L))
        .thenReturn(Optional.of(archivePersonalWork));

    ArchivePersonalWorkToggleResult result = service.deleteArchivePersonalWork(7L, 300L);

    assertThat(result.personalArtworkId()).isEqualTo(300L);
    assertThat(result.isArchived()).isFalse();
    verify(archivePersonalWorkRepository).delete(archivePersonalWork);
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
