package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchiveDisplayService {

  private final ArchiveDisplayRepository archiveDisplayRepository;

  public SaveArchiveDisplayService(ArchiveDisplayRepository archiveDisplayRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
  }

  @Transactional
  public ArchiveDisplayToggleResult saveArchiveDisplay(SaveArchiveDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    boolean alreadyArchived =
        archiveDisplayRepository
            .findByUserIdAndDisplayId(command.userId(), command.displayId())
            .isPresent();
    if (alreadyArchived) {
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_DISPLAY);
    }

    ArchiveDisplay archiveDisplay = ArchiveDisplay.create(command.displayId(), command.userId());
    try {
      archiveDisplayRepository.save(archiveDisplay);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 findByUserIdAndDisplayId 체크를 동시에 통과한 경우,
      // DB의 유니크 제약(V8)이 최종 방어선 역할을 함.
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_DISPLAY);
    }
    return new ArchiveDisplayToggleResult(command.displayId(), true);
  }
}
