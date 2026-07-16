package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.global.error.BusinessException;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchiveArtistService {

  private final ArchiveArtistRepository archiveArtistRepository;

  public SaveArchiveArtistService(ArchiveArtistRepository archiveArtistRepository) {
    this.archiveArtistRepository = archiveArtistRepository;
  }

  @Transactional
  public ArchiveArtistToggleResult saveArchiveArtist(SaveArchiveArtistCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    boolean alreadyArchived =
        archiveArtistRepository
            .findByUserIdAndCreatorId(command.userId(), command.creatorId())
            .isPresent();
    if (alreadyArchived) {
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST);
    }

    ArchiveArtist archiveArtist = ArchiveArtist.create(command.creatorId(), command.userId());
    try {
      archiveArtistRepository.save(archiveArtist);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 findByUserIdAndCreatorId 체크를 동시에 통과한 경우,
      // DB의 유니크 제약이 최종 방어선 역할을 함.
      // 단, FK 위반 등 다른 무결성 오류까지 중복 저장으로 오인하지 않도록
      // 유니크 제약(UQ_ARCHIVEARTIST_USER_CREATOR) 위반일 때만 변환한다.
      if (isUserCreatorUniqueConstraintViolation(e)) {
        throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST, e);
      }
      throw e;
    }
    return new ArchiveArtistToggleResult(command.creatorId(), true);
  }

  private boolean isUserCreatorUniqueConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_ARCHIVEARTIST_USER_CREATOR");
  }
}
