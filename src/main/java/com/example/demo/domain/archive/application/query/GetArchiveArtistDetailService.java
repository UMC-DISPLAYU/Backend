package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveArtistResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetArchiveArtistDetailService {

  private final ArchiveArtistRepository archiveArtistRepository;

  public GetArchiveArtistDetailService(ArchiveArtistRepository archiveArtistRepository) {
    this.archiveArtistRepository = archiveArtistRepository;
  }

  // TODO: Creator와 조인해서 name/field/profileImageUrl 등을 포함하도록 보강 필요. 지금은 목록조회와 동일한 얕은 정보만 반환.
  @Transactional(readOnly = true)
  public ArchiveArtistResult getArchiveArtistDetail(Long userId, Long archiveArtistId) {
    ArchiveArtist archiveArtist =
        archiveArtistRepository
            .findByIdAndUserId(archiveArtistId, userId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_ARTIST_NOT_FOUND));

    return new ArchiveArtistResult(
        archiveArtist.getId(),
        archiveArtist.getCreatorId(),
        archiveArtist.getUserId(),
        archiveArtist.getSavedAt());
  }
}
