package com.example.demo.domain.archive.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DeleteArchiveArtistServiceTest {

  private final ArchiveArtistRepository archiveArtistRepository =
      mock(ArchiveArtistRepository.class);
  private final DeleteArchiveArtistService service =
      new DeleteArchiveArtistService(archiveArtistRepository, new ArchivePermissionChecker());

  @Test
  void softDeletesArchiveArtistWhenItExistsByArtistUserId() {
    ArchiveArtist archiveArtist = ArchiveArtist.create(18L, 30L, 7L);
    when(archiveArtistRepository.findByUserIdAndArtistUserId(7L, 30L))
        .thenReturn(Optional.of(archiveArtist));

    ArchiveArtistToggleResult result = service.deleteArchiveArtist(7L, 30L);

    assertThat(result.artistUserId()).isEqualTo(30L);
    assertThat(result.isArchived()).isFalse();
    assertThat(archiveArtist.isDeleted()).isTrue();
    verify(archiveArtistRepository, never()).delete(archiveArtist);
  }

  @Test
  void rejectsWhenArchivedArtistDoesNotExist() {
    when(archiveArtistRepository.findByUserIdAndArtistUserId(7L, 30L)).thenReturn(Optional.empty());

    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> service.deleteArchiveArtist(7L, 30L))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(ArchiveErrorCode.ARCHIVE_ARTIST_NOT_FOUND));
  }
}
