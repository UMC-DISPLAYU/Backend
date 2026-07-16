package com.example.demo.domain.memo.application.command;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
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

  public DeleteExhibitionMemoService(
      ArchiveDisplayRepository archiveDisplayRepository, MemoRepository memoRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.memoRepository = memoRepository;
  }

  @Transactional
  public void deleteExhibitionMemo(Long userId, Long archiveDisplayId) {
    ArchiveDisplay archiveDisplay =
        archiveDisplayRepository
            .findById(archiveDisplayId)
            .filter(display -> display.getUserId().equals(userId))
            .orElseThrow(() -> new BusinessException(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));

    Memo memo =
        memoRepository
            .findByArchiveDisplayIdAndDeletedAtIsNull(archiveDisplay.getId())
            .orElseThrow(() -> new BusinessException(MemoErrorCode.MEMO_NOT_FOUND));

    memo.delete();
  }
}
