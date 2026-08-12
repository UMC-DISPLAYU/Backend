package com.example.demo.domain.archive.application.command;

import com.example.demo.domain.archive.application.result.ArchiveArtistToggleResult;
import com.example.demo.domain.archive.domain.aggregate.ArchiveArtist;
import com.example.demo.domain.archive.domain.error.ArchiveErrorCode;
import com.example.demo.domain.archive.domain.repository.ArchiveArtistRepository;
import com.example.demo.domain.artist.application.result.ArtistProfileSummaryResult;
import com.example.demo.domain.artist.application.usecase.GetArtistProfileSummariesByUserIdUseCase;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveArchiveArtistService {

  private final ArchiveArtistRepository archiveArtistRepository;
  private final GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase;

  public SaveArchiveArtistService(
      ArchiveArtistRepository archiveArtistRepository,
      GetArtistProfileSummariesByUserIdUseCase getArtistProfileSummariesByUserIdUseCase) {
    this.archiveArtistRepository = archiveArtistRepository;
    this.getArtistProfileSummariesByUserIdUseCase = getArtistProfileSummariesByUserIdUseCase;
  }

  @Transactional
  public ArchiveArtistToggleResult saveArchiveArtist(SaveArchiveArtistCommand command) {
    Objects.requireNonNull(command, "command must not be null.");

    // 프론트가 작가 프로필 화면에서 얻을 수 있는 값이 artistProfileId가 아니라 artistUserId뿐이라,
    // 여기서 실제 artistProfileId를 조회해 온다 (요청 경로변수 자체는 계속 artistId로 부르지만 값은 artistUserId다).
    List<ArtistProfileSummaryResult> summaries =
        getArtistProfileSummariesByUserIdUseCase.getArtistProfileSummariesByUserId(
            List.of(command.artistUserId()));
    if (summaries.isEmpty()) {
      throw new BusinessException(ArchiveErrorCode.ARTIST_PROFILE_NOT_FOUND);
    }
    Long artistProfileId = summaries.getFirst().artistProfileId();

    boolean alreadyArchived =
        archiveArtistRepository
            .findByUserIdAndArtistProfileId(command.userId(), artistProfileId)
            .isPresent();
    if (alreadyArchived) {
      throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST);
    }

    ArchiveArtist archiveArtist =
        ArchiveArtist.create(artistProfileId, command.artistUserId(), command.userId());
    try {
      archiveArtistRepository.save(archiveArtist);
    } catch (DataIntegrityViolationException e) {
      // 동시 요청으로 findByUserIdAndArtistProfileId 체크를 동시에 통과한 경우,
      // DB의 유니크 제약이 최종 방어선 역할을 함.
      // 단, FK 위반 등 다른 무결성 오류까지 중복 저장으로 오인하지 않도록
      // 유니크 제약(UQ_ARCHIVEARTIST_USER_ARTISTPROFILE) 위반일 때만 변환한다.
      if (isUserArtistProfileUniqueConstraintViolation(e)) {
        throw new BusinessException(ArchiveErrorCode.ALREADY_ARCHIVED_ARTIST, e);
      }
      throw e;
    }
    return new ArchiveArtistToggleResult(command.artistUserId(), true);
  }

  private boolean isUserArtistProfileUniqueConstraintViolation(DataIntegrityViolationException e) {
    String message = e.getMostSpecificCause().getMessage();
    return message != null && message.contains("UQ_ARCHIVEARTIST_USER_ARTISTPROFILE");
  }
}
