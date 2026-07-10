package com.example.demo.domain.archive.application.query;

import com.example.demo.domain.archive.application.result.ArchiveDisplayResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.global.error.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class GetArchiveDisplayDetailService {

  private final ArchiveDisplayRepository archiveDisplayRepository;

  public GetArchiveDisplayDetailService(ArchiveDisplayRepository archiveDisplayRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
  }

  // TODO: Display와 조인해서 title/posterImageUrl/장소/기간 등을 포함하도록 보강 필요. 지금은 목록조회와 동일한 얕은 정보만 반환.
  public ArchiveDisplayResult getArchiveDisplayDetail(Long archiveDisplayId) {
    ArchiveDisplay archiveDisplay =
        archiveDisplayRepository
            .findById(archiveDisplayId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_DISPLAY_NOT_FOUND));

    return new ArchiveDisplayResult(
        archiveDisplay.getId(), archiveDisplay.getDisplayId(), archiveDisplay.getUserId());
  }
}
