package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteArchiveArtistService {

  private final ArchiveArtistRepository archiveArtistRepository;

  public DeleteArchiveArtistService(ArchiveArtistRepository archiveArtistRepository) {
    this.archiveArtistRepository = archiveArtistRepository;
  }

  @Transactional
  public ArchiveArtistToggleResult deleteArchiveArtist(Long userId, Long artistProfileId) {
    ArchiveArtist archiveArtist =
        archiveArtistRepository
            .findByUserIdAndArtistProfileId(userId, artistProfileId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_ARTIST_NOT_FOUND));

    archiveArtistRepository.delete(archiveArtist);
    return new ArchiveArtistToggleResult(artistProfileId, false);
  }
}
