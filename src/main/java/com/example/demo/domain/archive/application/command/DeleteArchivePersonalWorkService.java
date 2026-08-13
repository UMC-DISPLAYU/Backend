package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteArchivePersonalWorkService {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository;

  public DeleteArchivePersonalWorkService(
      ArchivePersonalWorkRepository archivePersonalWorkRepository) {
    this.archivePersonalWorkRepository = archivePersonalWorkRepository;
  }

  @Transactional
  public ArchivePersonalWorkToggleResult deleteArchivePersonalWork(
      Long userId, Long personalArtworkId) {
    ArchivePersonalWork archivePersonalWork =
        archivePersonalWorkRepository
            .findByUserIdAndPersonalArtworkId(userId, personalArtworkId)
            .orElseThrow(
                () -> new BusinessException(ArchiveErrorCode.ARCHIVE_PERSONAL_WORK_NOT_FOUND));

    archivePersonalWork.delete();
    return new ArchivePersonalWorkToggleResult(personalArtworkId, false);
  }
}
