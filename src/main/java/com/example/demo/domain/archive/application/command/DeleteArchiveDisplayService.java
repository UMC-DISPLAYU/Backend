package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteArchiveDisplayService {

  private final ArchiveDisplayRepository archiveDisplayRepository;
  private final MemoRepository memoRepository;
  private final ArchivePermissionChecker archivePermissionChecker;

  public DeleteArchiveDisplayService(
      ArchiveDisplayRepository archiveDisplayRepository,
      MemoRepository memoRepository,
      ArchivePermissionChecker archivePermissionChecker) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.memoRepository = memoRepository;
    this.archivePermissionChecker = archivePermissionChecker;
  }

  @Transactional
  public ArchiveDisplayToggleResult deleteArchiveDisplay(Long userId, Long displayId) {
    ArchiveDisplay archiveDisplay =
        archiveDisplayRepository
            .findByUserIdAndDisplayId(userId, displayId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));
    archivePermissionChecker.requireOwner(archiveDisplay, userId);

    // Memo는 삭제돼도 row가 남아 FK로 ArchiveDisplay를 계속 참조하므로, 물리 삭제 전에
    // (이미 삭제된 것 포함) 먼저 정리하지 않으면 FK 제약 위반으로 저장 취소 자체가 실패한다.
    memoRepository.findByArchiveDisplayId(archiveDisplay.getId()).ifPresent(memoRepository::delete);

    archiveDisplayRepository.delete(archiveDisplay);
    return new ArchiveDisplayToggleResult(displayId, false);
  }
}
