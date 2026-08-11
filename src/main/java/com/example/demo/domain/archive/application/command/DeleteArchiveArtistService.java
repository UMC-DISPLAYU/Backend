package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
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
  private final ArchivePermissionChecker archivePermissionChecker;

  public DeleteArchiveArtistService(
      ArchiveArtistRepository archiveArtistRepository,
      ArchivePermissionChecker archivePermissionChecker) {
    this.archiveArtistRepository = archiveArtistRepository;
    this.archivePermissionChecker = archivePermissionChecker;
  }

  @Transactional
  public ArchiveArtistToggleResult deleteArchiveArtist(Long userId, Long artistUserId) {
    // artistUserId로 바로 찾는다 (ArtistProfile을 다시 조회하지 않음) — 작가가 이후 프로필을
    // 삭제해도 저장해 둔 사용자가 계속 취소할 수 있어야 하기 때문이다.
    ArchiveArtist archiveArtist =
        archiveArtistRepository
            .findByUserIdAndArtistUserId(userId, artistUserId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_ARTIST_NOT_FOUND));
    archivePermissionChecker.requireOwner(archiveArtist, userId);

    archiveArtistRepository.delete(archiveArtist);
    return new ArchiveArtistToggleResult(artistUserId, false);
  }
}
