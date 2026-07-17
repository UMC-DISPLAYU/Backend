package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.domain.memo.domain.aggregate.Memo;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.domain.memo.domain.repository.MemoRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteArtworkMemoService {

  private final ArchiveWorkRepository archiveWorkRepository;
  private final MemoRepository memoRepository;

  public DeleteArtworkMemoService(
      ArchiveWorkRepository archiveWorkRepository, MemoRepository memoRepository) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.memoRepository = memoRepository;
  }

  @Transactional
  public void deleteArtworkMemo(Long userId, Long archiveWorkId) {
    ArchiveWork archiveWork =
        archiveWorkRepository
            .findByIdAndUserId(archiveWorkId, userId)
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND));

    Memo memo =
        memoRepository
            .findByArchiveWorkIdAndDeletedAtIsNull(archiveWork.getId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.MEMO_NOT_FOUND));

    memo.delete();
  }
}
