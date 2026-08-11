package com.example.demo.domain.memo.application.permission;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import org.springframework.stereotype.Component;

@Component
public class MemoPermissionChecker {

  public void requireArchiveOwner(ArchiveDisplay archiveDisplay, Long userId) {
    if (!archiveDisplay.isOwnedBy(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }

  public void requireArchiveOwner(ArchiveWork archiveWork, Long userId) {
    if (!archiveWork.isOwnedBy(userId)) {
      throw new BusinessException(GlobalErrorCode.FORBIDDEN);
    }
  }
}
