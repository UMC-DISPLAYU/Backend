package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveDisplayArtworkExistenceRepository;
import com.example.demo.domain.archive.domain.repository.ArchiveWorkRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchiveWorkService {

  private final ArchiveWorkRepository archiveWorkRepository;
  private final ArchiveDisplayArtworkExistenceRepository displayArtworkExistenceRepository;

  public SaveArchiveWorkService(
      ArchiveWorkRepository archiveWorkRepository,
      ArchiveDisplayArtworkExistenceRepository displayArtworkExistenceRepository) {
    this.archiveWorkRepository = archiveWorkRepository;
    this.displayArtworkExistenceRepository = displayArtworkExistenceRepository;
  }

  @Transactional
  public ArchiveWorkToggleResult saveArchiveWork(SaveArchiveWorkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    if (!displayArtworkExistenceRepository.existsById(command.displayArtworkId())) {
      throw new BusinessException(ArchiveErrorCode.DISPLAY_ARTWORK_NOT_FOUND);
    }

    boolean alreadyArchived =
        archiveWorkRepository
            .findByUserIdAndDisplayArtworkId(command.userId(), command.displayArtworkId())
            .isPresent();
    if (alreadyArchived) {
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_WORK);
    }

    ArchiveWork archiveWork = ArchiveWork.create(command.displayArtworkId(), command.userId());
    try {
      archiveWorkRepository.save(archiveWork);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 findByUserIdAndDisplayArtworkId 체크를 동시에 통과한 경우,
      // DB의 유니크 제약이 최종 방어선 역할을 함.
      // 단, FK 위반 등 다른 무결성 오류까지 중복 저장으로 오인하지 않도록
      // 유니크 제약(UQ_ARCHIVEWORK_USER_DISPLAYARTWORK) 위반일 때만 변환한다.
      if (isUserDisplayArtworkUniqueConstraintViolation(e)) {
        throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_WORK, e);
      }
      throw e;
    }
    return new ArchiveWorkToggleResult(command.displayArtworkId(), true);
  }

  private boolean isUserDisplayArtworkUniqueConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_ARCHIVEWORK_USER_DISPLAYARTWORK");
  }
}
