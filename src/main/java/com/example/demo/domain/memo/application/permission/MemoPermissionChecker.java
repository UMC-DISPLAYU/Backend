package com.example.demo.domain.memo.application.permission;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.memo.domain.error.MemoErrorCode;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class MemoPermissionChecker {

  public void requireArchiveOwner(ArchiveDisplay archiveDisplay, Long userId) {
    if (!archiveDisplay.isOwnedBy(userId)) {
      throw new BusinessException(MemoErrorCode.ARCHIVE_DISPLAY_NOT_FOUND);
    }
  }

  public void requireArchiveOwner(ArchiveWork archiveWork, Long userId) {
    if (!archiveWork.isOwnedBy(userId)) {
      throw new BusinessException(MemoErrorCode.ARCHIVE_WORK_NOT_FOUND);
    }
  }

  public void requireArchiveOwner(ArchivePersonalWork archivePersonalWork, Long userId) {
    if (!archivePersonalWork.isOwnedBy(userId)) {
      throw new BusinessException(MemoErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND);
    }
  }
}
