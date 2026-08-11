package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteExhibitionMemoService {

  private final ArchiveDisplayRepository archiveDisplayRepository;
  private final MemoRepository memoRepository;
  private final MemoPermissionChecker memoPermissionChecker;

  public DeleteExhibitionMemoService(
      ArchiveDisplayRepository archiveDisplayRepository,
      MemoRepository memoRepository,
      MemoPermissionChecker memoPermissionChecker) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.memoRepository = memoRepository;
    this.memoPermissionChecker = memoPermissionChecker;
  }

  @Transactional
  public void deleteExhibitionMemo(Long userId, Long archiveDisplayId) {
    ArchiveDisplay archiveDisplay =
        archiveDisplayRepository
            .findById(archiveDisplayId)
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));
    memoPermissionChecker.requireArchiveOwner(archiveDisplay, userId);

    Memo memo =
        memoRepository
            .findByArchiveDisplayIdAndDeletedAtIsNull(archiveDisplay.getId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.MEMO_NOT_FOUND));

    memo.delete();
  }
}
