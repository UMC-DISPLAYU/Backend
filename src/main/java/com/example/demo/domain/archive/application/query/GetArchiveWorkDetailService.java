package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveWorkResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class GetArchiveWorkDetailService {

  private final ArchiveWorkRepository archiveWorkRepository;

  public GetArchiveWorkDetailService(ArchiveWorkRepository archiveWorkRepository) {
    this.archiveWorkRepository = archiveWorkRepository;
  }

  // TODO: DisplayArtwork와 조인해서 title/artistName/thumbnailUrl 등을 포함하도록 보강 필요. 지금은 목록조회와 동일한 얕은 정보만
  // 반환.
  public ArchiveWorkResult getArchiveWorkDetail(Long userId, Long archiveWorkId) {
    ArchiveWork archiveWork =
        archiveWorkRepository
            .findByIdAndUserId(archiveWorkId, userId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_WORK_NOT_FOUND));

    return new ArchiveWorkResult(
        archiveWork.getId(),
        archiveWork.getDisplayArtworkId(),
        archiveWork.getUserId(),
        archiveWork.getSavedAt());
  }
}
