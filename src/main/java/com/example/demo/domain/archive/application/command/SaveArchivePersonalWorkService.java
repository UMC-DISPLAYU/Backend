package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchivePersonalWorkToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchivePersonalWork;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchivePersonalWorkRepository;
import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkSummariesUseCase;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchivePersonalWorkService {

  private final ArchivePersonalWorkRepository archivePersonalWorkRepository;
  private final GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase;

  public SaveArchivePersonalWorkService(
      ArchivePersonalWorkRepository archivePersonalWorkRepository,
      GetPersonalArtworkSummariesUseCase getPersonalArtworkSummariesUseCase) {
    this.archivePersonalWorkRepository = archivePersonalWorkRepository;
    this.getPersonalArtworkSummariesUseCase = getPersonalArtworkSummariesUseCase;
  }

  @Transactional
  public ArchivePersonalWorkToggleResult saveArchivePersonalWork(
      SaveArchivePersonalWorkCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    boolean personalArtworkExists =
        !getPersonalArtworkSummariesUseCase
            .getPersonalArtworkSummaries(List.of(command.personalArtworkId()))
            .isEmpty();
    if (!personalArtworkExists) {
      throw new BusinessException(ArchiveErrorCode.PERSONAL_ARTWORK_NOT_FOUND);
    }

    boolean alreadyArchived =
        archivePersonalWorkRepository
            .findByUserIdAndPersonalArtworkId(command.userId(), command.personalArtworkId())
            .isPresent();
    if (alreadyArchived) {
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_PERSONAL_WORK);
    }

    ArchivePersonalWork archivePersonalWork =
        ArchivePersonalWork.create(command.personalArtworkId(), command.userId());
    try {
      archivePersonalWorkRepository.save(archivePersonalWork);
    } catch (DataIntegrityViolationException e) {
      if (isUserPersonalArtworkUniqueConstraintViolation(e)) {
        throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_PERSONAL_WORK, e);
      }
      throw e;
    }
    return new ArchivePersonalWorkToggleResult(command.personalArtworkId(), true);
  }

  private boolean isUserPersonalArtworkUniqueConstraintViolation(
      DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null
        && message.contains("UQ_ARCHIVEPERSONALWORK_ACTIVE_USER_PERSONALARTWORK");
  }
}
