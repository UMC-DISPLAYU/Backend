package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.permission.ArchivePermissionChecker;
import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteArchiveWorkService {

  private final ArchiveWorkRepository archiveWorkRepository;
  private final MemoRepository memoRepository;
  private final ArchivePermissionChecker archivePermissionChecker;

  public DeleteArchiveWorkService(
      ArchiveWorkRepository archiveWorkRepository,
      MemoRepository memoRepository,
      ArchivePermissionChecker archivePermissionChecker) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.memoRepository = memoRepository;
    this.archivePermissionChecker = archivePermissionChecker;
  }

  @Transactional
  public ArchiveWorkToggleResult deleteArchiveWork(Long userId, Long displayArtworkId) {
    ArchiveWork archiveWork =
        archiveWorkRepository
            .findByUserIdAndDisplayArtworkId(userId, displayArtworkId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_WORK_NOT_FOUND));
    archivePermissionChecker.requireOwner(archiveWork, userId);

    memoRepository
        .findByArchiveWorkIdAndDeletedAtIsNull(archiveWork.getId())
        .ifPresent(Memo::delete);
    archiveWork.delete();
    return new ArchiveWorkToggleResult(displayArtworkId, false);
  }
}
