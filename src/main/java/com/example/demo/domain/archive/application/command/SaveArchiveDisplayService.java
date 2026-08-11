package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveDisplayToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayExistenceRepository;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchiveDisplayService {

  private final ArchiveDisplayRepository archiveDisplayRepository;
  private final ArchiveDisplayExistenceRepository displayExistenceRepository;

  public SaveArchiveDisplayService(
      ArchiveDisplayRepository archiveDisplayRepository,
      ArchiveDisplayExistenceRepository displayExistenceRepository) {
    this.archiveDisplayRepository = archiveDisplayRepository;
    this.displayExistenceRepository = displayExistenceRepository;
  }

  @Transactional
  public ArchiveDisplayToggleResult saveArchiveDisplay(SaveArchiveDisplayCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    if (!displayExistenceRepository.existsById(command.displayId())) {
      throw new BusinessException(ArchiveErrorCode.DISPLAY_NOT_FOUND);
    }

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
      // 단, FK 위반 등 다른 무결성 오류까지 중복 저장으로 오인하지 않도록
      // 유니크 제약(UQ_ARCHIVEDISPLAY_USER_DISPLAY) 위반일 때만 변환한다.
      if (isUserDisplayUniqueConstraintViolation(e)) {
        throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_DISPLAY, e);
      }
      throw e;
    }
    return new ArchiveDisplayToggleResult(command.displayId(), true);
  }

  private boolean isUserDisplayUniqueConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_ARCHIVEDISPLAY_USER_DISPLAY");
  }
}
