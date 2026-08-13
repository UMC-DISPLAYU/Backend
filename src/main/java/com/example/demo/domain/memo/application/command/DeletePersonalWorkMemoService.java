package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.memo.application.permission.MemoPermissionChecker;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeletePersonalWorkMemoService {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository;
  private final MemoRepository memoRepository;
  private final MemoPermissionChecker memoPermissionChecker;

  public DeletePersonalWorkMemoService(
      ArchivePersonalWorkRepository archivePersonalWorkRepository,
      MemoRepository memoRepository,
      MemoPermissionChecker memoPermissionChecker) {
    this.archivePersonalWorkRepository = archivePersonalWorkRepository;
    this.memoRepository = memoRepository;
    this.memoPermissionChecker = memoPermissionChecker;
  }

  @Transactional
  public void deletePersonalWorkMemo(Long userId, Long archivePersonalWorkId) {
    ArchivePersonalWork archivePersonalWork =
        archivePersonalWorkRepository
            .findById(archivePersonalWorkId)
            .orElseThrow(
                () -> new BusinessException(MemoErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND));
    memoPermissionChecker.requireArchiveOwner(archivePersonalWork, userId);

    Memo memo =
        memoRepository
            .findByArchivePersonalWorkIdAndDeletedAtIsNull(archivePersonalWork.getId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.MEMO_NOT_FOUND));

    memo.delete();
  }
}
