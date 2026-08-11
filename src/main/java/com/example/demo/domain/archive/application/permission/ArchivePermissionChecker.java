package com.example.demo.domain.archive.application.permission;

import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Component;

@Component
public class ArchivePermissionChecker {

  public void requireOwner(ArchiveArtist archiveArtist, Long userId) {
    if (!archiveArtist.isOwnedBy(userId)) {
      throw new BusinessException(ArchiveErrorCode.ARCHIVE_ARTIST_NOT_FOUND);
    }
  }

  public void requireOwner(ArchiveDisplay archiveDisplay, Long userId) {
    if (!archiveDisplay.isOwnedBy(userId)) {
      throw new BusinessException(ArchiveErrorCode.ARCHIVE_DISPLAY_NOT_FOUND);
    }
  }

  public void requireOwner(ArchiveWork archiveWork, Long userId) {
    if (!archiveWork.isOwnedBy(userId)) {
      throw new BusinessException(ArchiveErrorCode.ARCHIVE_WORK_NOT_FOUND);
    }
  }
}
